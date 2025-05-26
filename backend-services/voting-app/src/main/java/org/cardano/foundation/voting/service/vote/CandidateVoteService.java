package org.cardano.foundation.voting.service.vote;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.micrometer.core.annotation.Timed;
import io.vavr.control.Either;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.cardano.foundation.voting.client.ChainFollowerClient;
import org.cardano.foundation.voting.client.UserVerificationClient;
import org.cardano.foundation.voting.domain.CandidatePayload;
import org.cardano.foundation.voting.domain.VoteReceipt;
import org.cardano.foundation.voting.domain.entity.Vote;
import org.cardano.foundation.voting.domain.entity.VoteMerkleProof;
import org.cardano.foundation.voting.domain.web3.*;
import org.cardano.foundation.voting.repository.VoteRepository;
import org.cardano.foundation.voting.service.auth.web3.CardanoWeb3Details;
import org.cardano.foundation.voting.service.auth.web3.KeriWeb3Details;
import org.cardano.foundation.voting.service.auth.web3.Web3AuthenticationToken;
import org.cardano.foundation.voting.service.auth.web3.Web3ConcreteDetails;
import org.cardano.foundation.voting.service.json.JsonService;
import org.cardano.foundation.voting.service.merkle_tree.MerkleProofSerdeService;
import org.cardano.foundation.voting.service.merkle_tree.VoteMerkleProofService;
import org.cardano.foundation.voting.utils.MoreUUID;
import org.cardanofoundation.merkle.ProofItem;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.zalando.problem.Problem;

import java.util.*;

import static com.bloxbean.cardano.client.util.HexUtil.encodeHexString;
import static org.cardano.foundation.voting.domain.VoteReceipt.Status.*;
import static org.cardano.foundation.voting.domain.VotingEventType.*;
import static org.cardano.foundation.voting.domain.web3.WalletType.KERI;
import static org.cardano.foundation.voting.domain.web3.Web3Action.*;
import static org.cardano.foundation.voting.utils.MoreNumber.isNumeric;
import static org.zalando.problem.Status.*;

@Service
@Slf4j
@RequiredArgsConstructor
public class CandidateVoteService {
    private static final int MAX_VOTES = 5;

    private final ObjectMapper objectMapper = new ObjectMapper();
    private final VoteRepository voteRepository;
    private final VoteMerkleProofService voteMerkleProofService;
    private final MerkleProofSerdeService merkleProofSerdeService;
    private final ChainFollowerClient chainFollowerClient;
    private final UserVerificationClient userVerificationClient;
    private final JsonService jsonService;

    @Transactional
    @Timed(value = "service.vote.castVote", histogram = true)
    public Either<Problem, Vote> castVote(Web3AuthenticationToken web3AuthenticationToken) {
        val concreteDetails = web3AuthenticationToken.getDetails();
        val details = concreteDetails.getWeb3CommonDetails();

        val event = details.getEvent();
        val eventId = event.id();
        val walletId = details.getWalletId();
        val walletType = details.getWalletType();

        val castVoteE = unwrapCastCoteEnvelope(concreteDetails);
        if (castVoteE.isLeft()) {
            return Either.left(castVoteE.getLeft());
        }

        val castVote = castVoteE.get();
        if (details.getAction() != CAST_VOTE) {
            return Either.left(Problem.builder()
                    .withTitle("INVALID_ACTION")
                    .withDetail("Action is not CAST_VOTE, expected action:" + CAST_VOTE.name())
                    .withStatus(BAD_REQUEST)
                    .build()
            );
        }

        if (event.isEventInactive()) {
            log.warn("Event is not active, id:{}", eventId);

            return Either.left(Problem.builder()
                    .withTitle("EVENT_IS_NOT_ACTIVE")
                    .withDetail("Event is not active (not started or already finished), id:" + eventId)
                    .withStatus(BAD_REQUEST)
                    .build());
        }

        if (details.getChainTip().isNotSynced()) {
            return Either.left(Problem.builder()
                    .withTitle("CHAIN_FOLLOWER_NOT_SYNCED")
                    .withDetail("Chain follower service not fully synced, please try again later!")
                    .withStatus(INTERNAL_SERVER_ERROR)
                    .build());
        }

        // check which is specific for the USER_BASED event type
        if (event.votingEventType() == USER_BASED) {
            val userVerifiedE = userVerificationClient.isVerified(eventId, walletType, details.getWalletId());
            if (userVerifiedE.isEmpty()) {
                return Either.left(Problem.builder()
                        .withTitle("ERROR_GETTING_USER_VERIFICATION_STATUS")
                        .withDetail("Unable to get user verification status from user-verification service, reason: user verification service not available")
                        .withStatus(INTERNAL_SERVER_ERROR)
                        .build()
                );
            }
            val userVerifiedResponse = userVerifiedE.get();

            if (userVerifiedResponse.isNotYetVerified()) {
                log.warn("User is not verified, id:{}", eventId);

                return Either.left(Problem.builder()
                        .withTitle("USER_IS_NOT_VERIFIED")
                        .withDetail("User is not verified, id:" + eventId)
                        .withStatus(BAD_REQUEST)
                        .build());
            }
        }

        val categoryId = castVote.getCategory();
        val categoryM = event.categoryDetailsById(categoryId);
        if (categoryM.isEmpty()) {
            log.warn("Unrecognised category, id:{}", categoryId);

            return Either.left(Problem.builder()
                    .withTitle("UNRECOGNISED_CATEGORY")
                    .withDetail("Unrecognised category, id:" + categoryId)
                    .withStatus(BAD_REQUEST)
                    .build());
        }
        val category = categoryM.orElseThrow();

        val proposalIdOrName = castVote.getProposal();

        ChainFollowerClient.ProposalDetailsResponse proposal;
        if (category.gdprProtection()) {
            val maybeProposal = category.findProposalById(proposalIdOrName);
            if (maybeProposal.isEmpty()) {
                log.warn("Unrecognised proposal, proposalId:{}", proposalIdOrName);

                return Either.left(Problem.builder()
                        .withTitle("UNRECOGNISED_PROPOSAL")
                        .withDetail("Unrecognised proposal, proposal:" + proposalIdOrName)
                        .withStatus(BAD_REQUEST)
                        .build());
            }
            proposal = maybeProposal.orElseThrow();
        } else {
            val maybeProposal = category.findProposalByName(proposalIdOrName);
            if (maybeProposal.isEmpty()) {
                log.warn("Unrecognised proposal, proposalId:{}", proposalIdOrName);

                return Either.left(Problem.builder()
                        .withTitle("UNRECOGNISED_PROPOSAL")
                        .withDetail("Unrecognised proposal, proposal:" + proposalIdOrName)
                        .withStatus(BAD_REQUEST)
                        .build());
            }
            proposal = maybeProposal.orElseThrow();
        }

        String voteId = castVote.getId();
        if (voteId == null || !MoreUUID.isUUIDv4(voteId)) {
            return Either.left(
                    Problem.builder()
                            .withTitle("INVALID_VOTE_ID")
                            .withDetail("Invalid vote voteId: " + voteId)
                            .withStatus(BAD_REQUEST)
                            .build());
        }

        val votedAtSlotStr = castVote.getVotedAt();
        if (!isNumeric(votedAtSlotStr)) {
            return Either.left(
                    Problem.builder()
                            .withTitle("INVALID_SLOT")
                            .withDetail("Vote's votedAt slot is not numeric!")
                            .withStatus(BAD_REQUEST)
                            .build()
            );
        }

        val votedAtSlot = castVote.getVotedAtSlot();

        val requestSlotE = concreteDetails.getRequestSlot();
        if (requestSlotE.isEmpty()) {
            return Either.left(
                    Problem.builder()
                            .withTitle("INVALID_SLOT")
                            .withDetail("Request slot is not numeric!")
                            .withStatus(BAD_REQUEST)
                            .build()
            );
        }

        val requestSlot = requestSlotE.get();
        if (votedAtSlot != requestSlot) {
            log.warn("Slots mismatch, votedAt slot:{}, envelope's slot:{}", votedAtSlot, concreteDetails.getRequestSlot());

            return Either.left(
                    Problem.builder()
                            .withTitle("SLOT_MISMATCH")
                            .withDetail("Request envelope's slot and votedAt slot mismatch!")
                            .withStatus(BAD_REQUEST)
                            .build()
            );
        }

        try {
            val payload = objectMapper.readValue(concreteDetails.getPayload(), CandidatePayload.class);
            if (payload == null || payload.getData() == null || payload.getData().getVotes() == null || payload.getData().getVotes().isEmpty()) {
                return Either.left(Problem.builder()
                        .withTitle("INVALID_CANDIDATE_VOTE_STRUCTURE")
                        .withDetail("Invalid candidate vote structure. It should be non empty list of candidate ids eg. 'votes: [1,4,7,9,13]'.")
                        .withStatus(BAD_REQUEST)
                        .build()
                );
            }
            val options = payload.getData().getVotes();
            if (options.size() > MAX_VOTES) {
                return Either.left(Problem.builder()
                        .withTitle("TOO_MANY_VOTES")
                        .withDetail("Too many votes. Maximum number of votes is: " + MAX_VOTES + ".")
                        .withStatus(BAD_REQUEST)
                        .build()
                );
            }
            Set<Long> uniqueVotes = new HashSet<>(options);
            if (uniqueVotes.size() != options.size()) {
                return Either.left(Problem.builder()
                        .withTitle("VOTES_NOT_UNIQUE")
                        .withDetail("Votes are not unique. Please remove duplicates from the candidate vote structure eg. 'votes: [1,4,7,9,13]'.")
                        .withStatus(BAD_REQUEST)
                        .build()
                );
            }
        } catch (JsonProcessingException e) {
            return Either.left(Problem.builder()
                    .withTitle("INVALID_CANDIDATE_VOTE_STRUCTURE")
                    .withDetail("Invalid candidate vote structure. It should be non empty list of candidate ids eg. 'votes: [1,4,7,9,13]'.")
                    .withStatus(BAD_REQUEST)
                    .build()
            );
        }

        val existingVoteM = voteRepository.findByEventIdAndCategoryIdAndWalletTypeAndWalletId(eventId, category.id(), walletType, walletId);
        if (existingVoteM.isPresent()) {
            if (!event.allowVoteChanging()) {
                return Either.left(Problem.builder()
                        .withTitle("VOTE_CANNOT_BE_CHANGED")
                        .withDetail("Vote cannot be changed for the address: " + walletId + ", within category: " + category.id() + ", for event: " + eventId)
                        .withStatus(BAD_REQUEST)
                        .build()
                );
            }
            val existingVote = existingVoteM.orElseThrow();

            val maybeLatestProof = voteMerkleProofService.findLatestProof(eventId, existingVoteM.orElseThrow().getId());
            if (maybeLatestProof.isPresent()) {
                log.warn("Cannot change existing vote for the address: " + walletId, ", within category: " + category.id() + ", for event: " + eventId);

                return Either.left(
                        Problem.builder()
                                .withTitle("VOTE_CANNOT_BE_CHANGED")
                                .withDetail("Vote cannot be changed for the address: " + walletId + ", within category: " + category.id() + ", for event: " + eventId)
                                .withStatus(BAD_REQUEST)
                                .build()
                );
            }
            existingVote.setId(existingVote.getId());
            existingVote.setProposalId(proposal.id());
            existingVote.setVotedAtSlot(castVote.getVotedAtSlot());
            existingVote.setWalletType(walletType);
            existingVote.setSignature(concreteDetails.getSignature());
            existingVote.setPayload(Optional.of(concreteDetails.getPayload()));
            existingVote.setPublicKey(concreteDetails.getPublicKey());

            return Either.right(voteRepository.saveAndFlush(existingVote));
        }

        val vote = new Vote();
        vote.setId(voteId);
        vote.setEventId(event.id());
        vote.setCategoryId(category.id());
        vote.setProposalId(proposal.id());
        vote.setWalletId(walletId);
        vote.setWalletType(walletType);
        vote.setVotedAtSlot(castVote.getVotedAtSlot());
        vote.setSignature(concreteDetails.getSignature());
        vote.setPayload(Optional.of(concreteDetails.getPayload()));
        vote.setPublicKey(concreteDetails.getPublicKey());
        vote.setIdNumericHash(UUID.fromString(voteId).hashCode() & 0xFFFFFFF);

        // KERI wallet type is not supported for account / balance voting events
        if (event.votingEventType() != USER_BASED && concreteDetails.getWeb3CommonDetails().getWalletType() == KERI) {
            return Either.left(
                    Problem.builder()
                            .withTitle("KERI_NOT_SUPPORTED")
                            .withDetail("Only Cardano wallet type supported for account / balance voting events is not supported.")
                            .withStatus(BAD_REQUEST)
                            .build()
            );
        }

        if (List.of(STAKE_BASED, BALANCE_BASED).contains(event.votingEventType())) {
            val accountE = chainFollowerClient.findAccount(eventId, walletType, walletId);
            if (accountE.isEmpty()) {
                return Either.left(Problem.builder()
                        .withTitle("ERROR_GETTING_ACCOUNT")
                        .withDetail("Unable to get account from chain-tip follower service, address:" + walletId)
                        .withStatus(INTERNAL_SERVER_ERROR)
                        .build()
                );
            }
            val maybeAccount = accountE.get();
            if (maybeAccount.isEmpty()) {
                log.warn("State account not eligible to vote, e.g. not staked or power is less than equal 0 for the address: " + walletId);

                return Either.left(
                        Problem.builder()
                                .withTitle("NOT_ELIGIBLE")
                                .withDetail("State account not eligible to vote, e.g. account not staked at snapshot epoch or voting power is less than equal 0 for the address:" + walletId)
                                .withStatus(BAD_REQUEST)
                                .build()
                );
            }
            val account = maybeAccount.get();

            // if we are eligible then we will have voting power
            val blockchainVotingPowerStr = account.votingPower();
            if (!isNumeric(blockchainVotingPowerStr)) {
                return Either.left(
                        Problem.builder()
                                .withTitle("INVALID_VOTING_POWER")
                                .withDetail("Invalid blockchain voting power for the address: " + walletId)
                                .withStatus(BAD_REQUEST)
                                .build()
                );
            }
            val blockchainVotingPower = Long.parseLong(blockchainVotingPowerStr);

            if (!isNumeric(castVote.getVotingPower().orElseThrow())) {
                return Either.left(
                        Problem.builder()
                                .withTitle("INVALID_VOTING_POWER")
                                .withDetail("Vote's votingPower is not numeric for the address: " + walletId)
                                .withStatus(BAD_REQUEST)
                                .build()
                );
            }
            val signedVotingPower = Long.parseLong(castVote.getVotingPower().orElseThrow());
            if (signedVotingPower != blockchainVotingPower) {
                return Either.left(
                        Problem.builder()
                                .withTitle("VOTING_POWER_MISMATCH")
                                .withDetail("Signed voting power is not equal to blockchain voting power for the stake address: " + walletId)
                                .withStatus(BAD_REQUEST)
                                .build()
                );
            }

            vote.setVotingPower(Optional.of(blockchainVotingPower));
        }

        if (event.votingEventType() == USER_BASED) {
            if (vote.getVotingPower().isPresent()) {
                return Either.left(
                        Problem.builder()
                                .withTitle("VOTING_POWER_NOT_SUPPORTED")
                                .withDetail("Voting power makes no sense for USER_BASED events, please remove it from the cast vote's envelope.")
                                .withStatus(BAD_REQUEST)
                                .build()
                );
            }
        }

        return Either.right(voteRepository.saveAndFlush(vote));
    }

    private Either<Problem, ViewVoteReceiptEnvelope> unwrapViewVoteReceiptEnvelope(Web3ConcreteDetails concreteDetails) {
        val signedJson = concreteDetails.getPayload();

        switch (concreteDetails) {
            case CardanoWeb3Details cardanoWeb3Details -> {
                Either<Problem, CIP93Envelope<ViewVoteReceiptEnvelope>> viewVoteEnvelopeE = jsonService.decodeCIP93ViewVoteReceiptEnvelope(signedJson);
                if (viewVoteEnvelopeE.isLeft()) {
                    return Either.left(
                            Problem.builder()
                                    .withTitle("INVALID_CIP93_DATA_SIGNATURE")
                                    .withDetail("Error while decoding view vote receipt signature!")
                                    .withStatus(BAD_REQUEST)
                                    .build()
                    );
                }

                return Either.right(viewVoteEnvelopeE.get().getData());
            }
            case KeriWeb3Details keriWeb3Details -> {
                Either<Problem, KERIEnvelope<ViewVoteReceiptEnvelope>> viewVoteEnvelopeE = jsonService.decodeKERIViewVoteReceiptEnvelope(signedJson);
                if (viewVoteEnvelopeE.isLeft()) {
                    return Either.left(
                            Problem.builder()
                                    .withTitle("INVALID_KERI_DATA_SIGNATURE")
                                    .withDetail("Error while decoding KERI view vote receipt signature!")
                                    .withStatus(BAD_REQUEST)
                                    .build()
                    );
                }

                return Either.right(viewVoteEnvelopeE.get().getData());
            }
            default -> {
                return Either.left(Problem.builder()
                        .withTitle("UNSUPPORTED_WALLET_TYPE")
                        .withDetail("Unsupported web3 details type:" + concreteDetails.getClass().getName())
                        .withStatus(BAD_REQUEST)
                        .build()
                );
            }
        }
    }

    private Either<Problem, VoteEnvelope> unwrapCastCoteEnvelope(Web3ConcreteDetails concreteDetails) {
        val signedJson = concreteDetails.getPayload();

        switch (concreteDetails) {
            case CardanoWeb3Details cardanoWeb3Details -> {
                val castVoteRequestBodyJsonE = jsonService.decodeCIP93VoteEnvelope(signedJson);
                if (castVoteRequestBodyJsonE.isLeft()) {
                    if (castVoteRequestBodyJsonE.isLeft()) {
                        return Either.left(
                                Problem.builder()
                                        .withTitle("INVALID_CIP93_DATA_SIGNATURE")
                                        .withDetail("Error while decoding cast vote signature!")
                                        .withStatus(BAD_REQUEST)
                                        .build()
                        );
                    }
                }

                val cip93CastVoteEnvelope = castVoteRequestBodyJsonE.get();

                return Either.right(cip93CastVoteEnvelope.getData());
            }
            case KeriWeb3Details keriWeb3Details -> {
                val castVoteRequestBodyJsonE = jsonService.decodeKERIVoteEnvelope(signedJson);
                if (castVoteRequestBodyJsonE.isLeft()) {
                    return Either.left(
                            Problem.builder()
                                    .withTitle("INVALID_KERI_DATA_SIGNATURE")
                                    .withDetail("Error while decoding KERI cast vote signature!")
                                    .withStatus(BAD_REQUEST)
                                    .build()
                    );
                }
                val keriCastVoteEnvelope = castVoteRequestBodyJsonE.get();

                return Either.right(keriCastVoteEnvelope.getData());
            }
            default -> {
                return Either.left(Problem.builder()
                        .withTitle("UNSUPPORTED_WEB3_DETAILS")
                        .withDetail("Unsupported web3 details type:" + concreteDetails.getWeb3CommonDetails().getWalletType())
                        .withStatus(BAD_REQUEST)
                        .build()
                );
            }
        }
    }

    @Transactional(readOnly = true)
    @Timed(value = "service.vote.voteReceipt", histogram = true)
    public Either<Problem, VoteReceipt> voteReceipt(Web3AuthenticationToken web3AuthenticationToken) {
        log.info("Fetching voter's receipt for the signed data...");

        val concreteDetails = web3AuthenticationToken.getDetails();
        val commonDetails = web3AuthenticationToken.getDetails().getWeb3CommonDetails();

        val event = commonDetails.getEvent();
        val walletType = commonDetails.getWalletType();
        val walletId = commonDetails.getWalletId();

        val viewVoteReceiptEnvelopeE = unwrapViewVoteReceiptEnvelope(concreteDetails);
        if (viewVoteReceiptEnvelopeE.isLeft()) {
            return Either.left(viewVoteReceiptEnvelopeE.getLeft());
        }

        val viewVoteReceiptEnvelope = viewVoteReceiptEnvelopeE.get();

        if (commonDetails.getAction() != VIEW_VOTE_RECEIPT) {
            return Either.left(Problem.builder()
                    .withTitle("INVALID_ACTION")
                    .withDetail("Action is not VIEW_VOTE_RECEIPT, action:" + commonDetails.getAction())
                    .withStatus(BAD_REQUEST)
                    .build()
            );
        }

        val categoryId = viewVoteReceiptEnvelope.getCategory();

        return actualVoteReceipt(event, categoryId, walletType, walletId);
    }

    private Either<Problem, VoteReceipt> actualVoteReceipt(ChainFollowerClient.EventDetailsResponse event,
                                                           String categoryId,
                                                           WalletType walletType,
                                                           String walletId) {
        val categoryM = event.categoryDetailsById(categoryId);
        if (categoryM.isEmpty()) {
            log.warn("Unrecognised category, id:{}", categoryId);

            return Either.left(Problem.builder()
                    .withTitle("UNRECOGNISED_CATEGORY")
                    .withDetail("Unrecognised category, id:" + categoryId)
                    .withStatus(BAD_REQUEST)
                    .build());
        }
        val category = categoryM.orElseThrow();

        val voteM = voteRepository.findByEventIdAndCategoryIdAndWalletTypeAndWalletId(event.id(), category.id(), walletType, walletId);
        if (voteM.isEmpty()) {
            return Either.left(
                    Problem.builder()
                            .withTitle("VOTE_NOT_FOUND")
                            .withDetail("Not voted yet for stakeKey:" + walletId)
                            .withStatus(NOT_FOUND)
                            .build()
            );
        }
        val vote = voteM.orElseThrow();

        val proposalM = category.findProposalById(vote.getProposalId());
        if (proposalM.isEmpty()) {
            return Either.left(
                    Problem.builder()
                            .withTitle("PROPOSAL_NOT_FOUND")
                            .withDetail("Proposal not found for voteId:" + vote.getId())
                            .withStatus(NOT_FOUND)
                            .build()
            );
        }
        val proposal = proposalM.orElseThrow();
        val proposalIdOrName = category.gdprProtection() ? proposal.id() : proposal.name();

        val latestVoteMerkleProof = voteMerkleProofService.findLatestProof(event.id(), vote.getId());

        return latestVoteMerkleProof.map(proof -> {
            log.info("Latest merkle proof found for voteId:{}", vote.getId());

            val transactionDetailsE = chainFollowerClient.getTransactionDetails(proof.getL1TransactionHash());
            if (transactionDetailsE.isEmpty()) {
                return Either.<Problem, VoteReceipt>left(Problem.builder()
                        .withTitle("ERROR_GETTING_TRANSACTION_DETAILS")
                        .withDetail("Unable to get transaction details from chain-tip follower service, transactionHash:" + proof.getL1TransactionHash())
                        .withStatus(INTERNAL_SERVER_ERROR)
                        .build());
            }
            val transactionDetailsM = transactionDetailsE.get();

            val isL1CommitmentOnChain = transactionDetailsM.map(ChainFollowerClient.TransactionDetailsResponse::finalityScore);

            return Either.<Problem, VoteReceipt>right(VoteReceipt.builder()
                    .id(vote.getId())
                    .event(event.id())
                    .category(category.id())
                    .proposal(proposalIdOrName)
                    .signature(vote.getSignature())
                    .payload(vote.getPayload())
                    .publicKey(vote.getPublicKey())
                    .votedAtSlot(Long.valueOf(vote.getVotedAtSlot()).toString())
                    .walletId(vote.getWalletId())
                    .walletType(vote.getWalletType())
                    .votingPower(vote.getVotingPower().map(String::valueOf))
                    .status(readMerkleProofStatus(proof, isL1CommitmentOnChain))
                    .finalityScore(isL1CommitmentOnChain)
                    .merkleProof(convertMerkleProof(proof, transactionDetailsM))
                    .build());

        }).orElseGet(() -> {
            log.info("Merkle proof not found yet for voteId:{}", vote.getId());

            return Either.right(VoteReceipt.builder()
                    .id(vote.getId())
                    .event(event.id())
                    .category(category.id())
                    .proposal(proposalIdOrName)
                    .signature(vote.getSignature())
                    .payload(vote.getPayload())
                    .publicKey(vote.getPublicKey())
                    .votedAtSlot(Long.valueOf(vote.getVotedAtSlot()).toString())
                    .walletId(vote.getWalletId())
                    .walletType(vote.getWalletType())
                    .votingPower(vote.getVotingPower().map(String::valueOf))
                    .status(BASIC)
                    .build()
            );
        });
    }

    private static VoteReceipt.Status readMerkleProofStatus(VoteMerkleProof merkleProof,
                                                            Optional<ChainFollowerClient.FinalityScore> isL1CommitmentOnChain) {
        if (merkleProof.isInvalidated()) {
            return ROLLBACK;
        }

        return isL1CommitmentOnChain.isEmpty() ? PARTIAL : FULL;
    }

    private VoteReceipt.MerkleProof convertMerkleProof(VoteMerkleProof proof,
                                                       Optional<ChainFollowerClient.TransactionDetailsResponse> transactionDetails) {
        return VoteReceipt.MerkleProof.builder()
                .blockHash(transactionDetails.map(ChainFollowerClient.TransactionDetailsResponse::blockHash))
                .absoluteSlot(transactionDetails.map(ChainFollowerClient.TransactionDetailsResponse::absoluteSlot))
                .rootHash(proof.getRootHash())
                .transactionHash(proof.getL1TransactionHash())
                .steps(convertSteps(proof))
                .build();

    }

    private List<VoteReceipt.MerkleProofItem> convertSteps(VoteMerkleProof proof) {
        return merkleProofSerdeService.deserialise(proof.getProofItemsJson()).stream().map(item -> {

            if (item instanceof ProofItem.Left pl) {
                return VoteReceipt.MerkleProofItem.builder()
                        .type(VoteReceipt.MerkleProofType.L)
                        .hash(encodeHexString(pl.hash()))
                        .build();
            }

            if (item instanceof ProofItem.Right pr) {
                return VoteReceipt.MerkleProofItem.builder()
                        .type(VoteReceipt.MerkleProofType.R)
                        .hash(encodeHexString(pr.hash()))
                        .build();
            }

            throw new RuntimeException("Unknown proof item type:" + item.getClass().getName());
        }).toList();
    }
}
