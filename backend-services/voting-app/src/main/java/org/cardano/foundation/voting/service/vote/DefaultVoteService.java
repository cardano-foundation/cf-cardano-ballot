package org.cardano.foundation.voting.service.vote;

import io.micrometer.core.annotation.Timed;
import io.vavr.control.Either;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.cardano.foundation.voting.client.ChainFollowerClient;
import org.cardano.foundation.voting.client.UserVerificationClient;
import org.cardano.foundation.voting.domain.CategoryProposalPair;
import org.cardano.foundation.voting.domain.VoteReceipt;
import org.cardano.foundation.voting.domain.entity.Vote;
import org.cardano.foundation.voting.domain.entity.VoteMerkleProof;
import org.cardano.foundation.voting.repository.VoteRepository;
import org.cardano.foundation.voting.service.auth.jwt.JwtAuthenticationToken;
import org.cardano.foundation.voting.service.auth.web3.Web3AuthenticationToken;
import org.cardano.foundation.voting.service.json.JsonService;
import org.cardano.foundation.voting.service.merkle_tree.MerkleProofSerdeService;
import org.cardano.foundation.voting.service.merkle_tree.VoteMerkleProofService;
import org.cardano.foundation.voting.utils.MoreUUID;
import org.cardanofoundation.merkle.ProofItem;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.zalando.problem.Problem;

import java.util.List;
import java.util.Optional;

import static com.bloxbean.cardano.client.util.HexUtil.encodeHexString;
import static org.cardano.foundation.voting.client.ChainFollowerClient.AccountStatus.NOT_ELIGIBLE;
import static org.cardano.foundation.voting.domain.VoteReceipt.Status.*;
import static org.cardano.foundation.voting.domain.VotingEventType.*;
import static org.cardano.foundation.voting.domain.web3.Web3Action.*;
import static org.cardano.foundation.voting.utils.MoreNumber.isNumeric;
import static org.cardanofoundation.cip30.MessageFormat.TEXT;
import static org.zalando.problem.Status.*;

@Service
@Slf4j
@RequiredArgsConstructor
public class DefaultVoteService implements VoteService {

    private final VoteRepository voteRepository;

    private final VoteMerkleProofService voteMerkleProofService;

    private final MerkleProofSerdeService merkleProofSerdeService;

    private final ChainFollowerClient chainFollowerClient;

    private final UserVerificationClient userVerificationClient;

    private final JsonService jsonService;

    @Override
    @Transactional(readOnly = true)
    @Timed(value = "service.vote.getVotedOn", histogram = true)
    public Either<Problem, List<CategoryProposalPair>> getVotedOn(JwtAuthenticationToken auth) {
        var jwtEventId = auth.eventDetails().id();
        var jwtStakeAddress = auth.getStakeAddress();

        if (auth.isActionNotAllowed(VOTED_ON)) {
            return Either.left(Problem.builder()
                    .withTitle("ACTION_NOT_ALLOWED")
                    .withDetail("Action VOTED_ON not allowed for the role:" + auth.role().name())
                    .withStatus(BAD_REQUEST)
                    .build());
        }

        var votedOn = voteRepository.getVotedOn(jwtEventId, jwtStakeAddress).stream()
                .map(r -> new CategoryProposalPair(r.getCategoryId(), r.getCategoryId())).toList();

        return Either.right(votedOn);
    }

    @Override
    @Transactional(readOnly = true)
    @Timed(value = "service.vote.findAllCompactVotesByEventId", histogram = true)
    public List<VoteRepository.CompactVote> findAllCompactVotesByEventId(String eventId) {
        return voteRepository.findAllCompactVotesByEventId(eventId);
    }

    @Transactional(readOnly = true)
    @Timed(value = "service.vote.isVoteChangingPossible", histogram = true)
    public Either<Problem, Boolean> isVoteChangingPossible(String voteId,
                                                           JwtAuthenticationToken auth) {
        var jwtEventId = auth.eventDetails().id();

        if (auth.isActionNotAllowed(IS_VOTE_CHANGING_ALLOWED)) {
            return Either.left(Problem.builder()
                    .withTitle("ACTION_NOT_ALLOWED")
                    .withDetail("Action IS_VOTE_CASTING_ALLOWED not allowed for the role:" + auth.role().name())
                    .withStatus(BAD_REQUEST)
                    .build());
        }
        if (auth.eventDetails().isEventInactive()) {
            return Either.right(false);
        }

        var maybeExistingVote = voteRepository.findById(voteId);
        if (maybeExistingVote.isEmpty()) {
            return Either.left(Problem.builder()
                    .withTitle("VOTE_NOT_FOUND")
                    .withDetail("Vote not found, voteId:" + voteId)
                    .withStatus(BAD_REQUEST)
                    .build()
            );
        }

        var maybeExistingProof = voteMerkleProofService.findLatestProof(jwtEventId, voteId);
        if (maybeExistingProof.isPresent()) {
            return Either.left(Problem.builder()
                    .withTitle("VOTE_CANNOT_BE_CHANGED")
                    .withDetail("Vote cannot be changed, voteId:" + voteId)
                    .withStatus(BAD_REQUEST)
                    .build()
            );
        }

        return Either.right(true);
    }

    @Override
    @Transactional
    @Timed(value = "service.vote.castVote", histogram = true)
    public Either<Problem, Vote> castVote(Web3AuthenticationToken web3AuthenticationToken) {
        var details = web3AuthenticationToken.getDetails();
        var cip30VerificationResult = details.getCip30VerificationResult();

        var event = details.getEvent();
        var eventId = event.id();
        var stakeAddress = details.getStakeAddress();

        var signedJson = cip30VerificationResult.getMessage(TEXT);

        var castVoteRequestBodyJsonE = jsonService.decodeCIP93VoteEnvelope(signedJson);
        if (castVoteRequestBodyJsonE.isLeft()) {
            if (castVoteRequestBodyJsonE.isLeft()) {
                return Either.left(
                        Problem.builder()
                                .withTitle("INVALID_CIP30_DATA_SIGNATURE")
                                .withDetail("Invalid cast vote signature!")
                                .withStatus(BAD_REQUEST)
                                .build()
                );
            }
        }
        var cip93VoteEnvelope = castVoteRequestBodyJsonE.get();

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

        // check which is specific for the USER_BASED event type
        if (event.votingEventType() == USER_BASED) {
            var userVerifiedE = userVerificationClient.isVerified(eventId, details.getStakeAddress());
            if (userVerifiedE.isEmpty()) {
                return Either.left(Problem.builder()
                        .withTitle("ERROR_GETTING_USER_VERIFICATION_STATUS")
                        .withDetail("Unable to get user verification status from user-verification service, reason: user verification service not available")
                        .withStatus(INTERNAL_SERVER_ERROR)
                        .build()
                );
            }
            var userVerifiedResponse = userVerifiedE.get();

            if (userVerifiedResponse.isNotYetVerified()) {
                log.warn("User is not verified, id:{}", eventId);

                return Either.left(Problem.builder()
                        .withTitle("USER_IS_NOT_VERIFIED")
                        .withDetail("User is not verified, id:" + eventId)
                        .withStatus(BAD_REQUEST)
                        .build());
            }
        }

        var categoryId = cip93VoteEnvelope.getData().getCategory();
        var maybeCategory = event.categoryDetailsById(categoryId);
        if (maybeCategory.isEmpty()) {
            log.warn("Unrecognised category, id:{}", categoryId);

            return Either.left(Problem.builder()
                    .withTitle("UNRECOGNISED_CATEGORY")
                    .withDetail("Unrecognised category, id:" + categoryId)
                    .withStatus(BAD_REQUEST)
                    .build());
        }
        var category = maybeCategory.orElseThrow();

        var proposalIdOrName = cip93VoteEnvelope.getData().getProposal();

        ChainFollowerClient.ProposalDetailsResponse proposal;
        if (category.gdprProtection()) {
            var maybeProposal = category.findProposalById(proposalIdOrName);
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
            var maybeProposal = category.findProposalByName(proposalIdOrName);
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

        String voteId = cip93VoteEnvelope.getData().getId();
        if (voteId == null || !MoreUUID.isUUIDv4(voteId)) {
            return Either.left(
                    Problem.builder()
                            .withTitle("INVALID_VOTE_ID")
                            .withDetail("Invalid vote voteId: " + voteId)
                            .withStatus(BAD_REQUEST)
                            .build());
        }
        var votedAtSlotStr = cip93VoteEnvelope.getData().getVotedAt();
        if (!isNumeric(votedAtSlotStr)) {
            return Either.left(
                    Problem.builder()
                            .withTitle("INVALID_SLOT")
                            .withDetail("Vote's votedAt slot is not numeric!")
                            .withStatus(BAD_REQUEST)
                            .build()
            );
        }
        var votedAtSlot = cip93VoteEnvelope.getData().getVotedAtSlot();

        if (votedAtSlot != details.getEnvelope().getSlotAsLong()) {
            log.warn("Slots mismatch, votedAt slot:{}, CIP-93 slot:{}", votedAtSlot, details.getEnvelope().getSlotAsLong());

            return Either.left(
                    Problem.builder()
                            .withTitle("SLOT_MISMATCH")
                            .withDetail("CIP93 envelope slot and votedAt slot mismatch!")
                            .withStatus(BAD_REQUEST)
                            .build()
            );
        }

        var maybeExistingVote = voteRepository.findByEventIdAndCategoryIdAndVoterStakingAddress(eventId, category.id(), details.getStakeAddress());
        if (maybeExistingVote.isPresent()) {

            if (!event.allowVoteChanging()) {
                return Either.left(Problem.builder()
                        .withTitle("VOTE_CANNOT_BE_CHANGED")
                        .withDetail("Vote cannot be changed for the stake address: " + stakeAddress + ", within category: " + category.id() + ", for event: " + eventId)
                        .withStatus(BAD_REQUEST)
                        .build()
                );
            }
            var existingVote = maybeExistingVote.orElseThrow();

            var maybeLatestProof = voteMerkleProofService.findLatestProof(eventId, maybeExistingVote.orElseThrow().getId());
            if (maybeLatestProof.isPresent()) {
                log.warn("Cannot change existing vote for the stake address: " + stakeAddress, ", within category: " + category.id() + ", for event: " + eventId);

                return Either.left(
                        Problem.builder()
                                .withTitle("VOTE_CANNOT_BE_CHANGED")
                                .withDetail("Vote cannot be changed for the stake address: " + stakeAddress + ", within category: " + category.id() + ", for event: " + eventId)
                                .withStatus(BAD_REQUEST)
                                .build()
                );
            }
            existingVote.setId(existingVote.getId());
            existingVote.setProposalId(proposal.id());
            existingVote.setVotedAtSlot(cip93VoteEnvelope.getSlotAsLong());
            existingVote.setCoseSignature(details.getSignedWeb3Request().getCoseSignature());
            existingVote.setCosePublicKey(details.getSignedWeb3Request().getCosePublicKey());

            return Either.right(voteRepository.saveAndFlush(existingVote));
        }

        var vote = new Vote();
        vote.setId(voteId);
        vote.setEventId(event.id());
        vote.setCategoryId(category.id());
        vote.setProposalId(proposal.id());
        vote.setVoterStakingAddress(stakeAddress);
        vote.setVotedAtSlot(cip93VoteEnvelope.getSlotAsLong());
        vote.setCoseSignature(details.getSignedWeb3Request().getCoseSignature());
        vote.setCosePublicKey(details.getSignedWeb3Request().getCosePublicKey());

        if (List.of(STAKE_BASED, BALANCE_BASED).contains(event.votingEventType())) {
            var accountE = chainFollowerClient.findAccount(eventId, stakeAddress);
            if (accountE.isEmpty()) {
                return Either.left(Problem.builder()
                        .withTitle("ERROR_GETTING_ACCOUNT")
                        .withDetail("Unable to get account from chain-tip follower service, stakeAddress:" + stakeAddress)
                        .withStatus(INTERNAL_SERVER_ERROR)
                        .build()
                );
            }
            var maybeAccount = accountE.get();
            if (maybeAccount.isEmpty()) {
                log.warn("Account not found for the stake address: " + stakeAddress);

                return Either.left(
                        Problem.builder()
                                .withTitle("ACCOUNT_NOT_FOUND")
                                .withDetail("Account not found for the stake address:" + stakeAddress)
                                .withStatus(BAD_REQUEST)
                                .build()
                );
            }
            var account = maybeAccount.get();

            if (account.accountStatus() == NOT_ELIGIBLE) {
                log.warn("State account not eligible to vote, e.g. not staked or power is less than equal 0 for the stake address: " + stakeAddress);

                return Either.left(
                        Problem.builder()
                                .withTitle("NOT_ELIGIBLE")
                                .withDetail("State account not eligible to vote, e.g. account not staked at snapshot epoch or voting power is less than equal 0 for the stake address:" + stakeAddress)
                                .withStatus(BAD_REQUEST)
                                .build()
                );
            }

            // if we are eligible then we will have voting power
            var blockchainVotingPowerStr = account.votingPower().orElseThrow();
            if (!isNumeric(blockchainVotingPowerStr)) {
                return Either.left(
                        Problem.builder()
                                .withTitle("INVALID_VOTING_POWER")
                                .withDetail("Invalid blockchain voting power for the stake address: " + stakeAddress)
                                .withStatus(BAD_REQUEST)
                                .build()
                );
            }
            var blockchainVotingPower = Long.parseLong(blockchainVotingPowerStr);

            if (!isNumeric(cip93VoteEnvelope.getData().getVotingPower().orElseThrow())) {
                return Either.left(
                        Problem.builder()
                                .withTitle("INVALID_VOTING_POWER")
                                .withDetail("CIP-93's envelope votingPower is not numeric for the stake address: " + stakeAddress)
                                .withStatus(BAD_REQUEST)
                                .build()
                );
            }
            var signedVotingPower = Long.parseLong(cip93VoteEnvelope.getData().getVotingPower().orElseThrow());
            if (signedVotingPower != blockchainVotingPower) {
                return Either.left(
                        Problem.builder()
                                .withTitle("VOTING_POWER_MISMATCH")
                                .withDetail("Signed voting power is not equal to blockchain voting power for the stake address: " + stakeAddress)
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

    @Override
    @Transactional(readOnly = true)
    @Timed(value = "service.vote.voteReceipt", histogram = true)
    public Either<Problem, VoteReceipt> voteReceipt(Web3AuthenticationToken web3AuthenticationToken) {
        log.info("Fetching voter's receipt for the signed data...");

        var details = web3AuthenticationToken.getDetails();
        var cip30VerificationResult = details.getCip30VerificationResult();

        var event = details.getEvent();
        var stakeAddress = details.getStakeAddress();

        var signedJson = cip30VerificationResult.getMessage(TEXT);

        var viewVoteReceiptEnvelopeE = jsonService.decodeCIP93ViewVoteReceiptEnvelope(signedJson);
        if (viewVoteReceiptEnvelopeE.isLeft()) {
            return Either.left(
                    Problem.builder()
                            .withTitle("INVALID_CIP30_DATA_SIGNATURE")
                            .withDetail("Invalid view vote receipt signature!")
                            .withStatus(BAD_REQUEST)
                            .build()
            );
        }
        var viewVoteReceiptEnvelope = viewVoteReceiptEnvelopeE.get();

        if (details.getAction() != VIEW_VOTE_RECEIPT) {
            return Either.left(Problem.builder()
                    .withTitle("INVALID_ACTION")
                    .withDetail("Action is not VIEW_VOTE_RECEIPT, action:" + details.getAction())
                    .withStatus(BAD_REQUEST)
                    .build()
            );
        }

        var categoryId = viewVoteReceiptEnvelope.getData().getCategory();

        return actualVoteReceipt(event, categoryId, stakeAddress);
    }

    @Override
    @Transactional(readOnly = true)
    public Either<Problem, VoteReceipt> voteReceipt(String categoryId,
                                                    JwtAuthenticationToken auth) {
        var jwtStakeAddress = auth.getStakeAddress();

        if (auth.isActionNotAllowed(VIEW_VOTE_RECEIPT)) {
            return Either.left(Problem.builder()
                    .withTitle("ACTION_NOT_ALLOWED")
                    .withDetail("Action VIEW_VOTE_RECEIPT not allowed for the role:" + auth.role().name())
                    .withStatus(BAD_REQUEST)
                    .build());
        }

        return actualVoteReceipt(auth.eventDetails(), categoryId, jwtStakeAddress);
    }

    private Either<Problem, VoteReceipt> actualVoteReceipt(ChainFollowerClient.EventDetailsResponse event,
                                                           String categoryId,
                                                           String stakeAddress) {
        var maybeCategory = event.categoryDetailsById(categoryId);
        if (maybeCategory.isEmpty()) {
            log.warn("Unrecognised category, id:{}", categoryId);

            return Either.left(Problem.builder()
                    .withTitle("UNRECOGNISED_CATEGORY")
                    .withDetail("Unrecognised category, id:" + categoryId)
                    .withStatus(BAD_REQUEST)
                    .build());
        }
        var category = maybeCategory.orElseThrow();

        var maybeVote = voteRepository.findByEventIdAndCategoryIdAndVoterStakingAddress(event.id(), category.id(), stakeAddress);
        if (maybeVote.isEmpty()) {
            return Either.left(
                    Problem.builder()
                            .withTitle("VOTE_NOT_FOUND")
                            .withDetail("Not voted yet for stakeKey:" + stakeAddress)
                            .withStatus(NOT_FOUND)
                            .build()
            );
        }
        var vote = maybeVote.orElseThrow();

        var maybeProposal = category.findProposalById(vote.getProposalId());
        if (maybeProposal.isEmpty()) {
            return Either.left(
                    Problem.builder()
                            .withTitle("PROPOSAL_NOT_FOUND")
                            .withDetail("Proposal not found for voteId:" + vote.getId())
                            .withStatus(NOT_FOUND)
                            .build()
            );
        }
        var proposal = maybeProposal.orElseThrow();
        var proposalIdOrName = category.gdprProtection() ? proposal.id() : proposal.name();

        var latestVoteMerkleProof = voteMerkleProofService.findLatestProof(event.id(), vote.getId());

        return latestVoteMerkleProof.map(proof -> {
            log.info("Latest merkle proof found for voteId:{}", vote.getId());

            var transactionDetailsE = chainFollowerClient.getTransactionDetails(proof.getL1TransactionHash());
            if (transactionDetailsE.isEmpty()) {
                return Either.<Problem, VoteReceipt>left(Problem.builder()
                        .withTitle("ERROR_GETTING_TRANSACTION_DETAILS")
                        .withDetail("Unable to get transaction details from chain-tip follower service, transactionHash:" + proof.getL1TransactionHash())
                        .withStatus(INTERNAL_SERVER_ERROR)
                        .build());
            }
            var maybeTransactionDetails = transactionDetailsE.get();

            var isL1CommitmentOnChain = maybeTransactionDetails.map(ChainFollowerClient.TransactionDetailsResponse::finalityScore);

            return Either.<Problem, VoteReceipt>right(VoteReceipt.builder()
                    .id(vote.getId())
                    .event(event.id())
                    .category(category.id())
                    .proposal(proposalIdOrName)
                    .coseSignature(vote.getCoseSignature())
                    .cosePublicKey(vote.getCosePublicKey())
                    .votedAtSlot(Long.valueOf(vote.getVotedAtSlot()).toString())
                    .voterStakingAddress(vote.getVoterStakingAddress())
                    .votingPower(vote.getVotingPower().map(String::valueOf))
                    .status(readMerkleProofStatus(proof, isL1CommitmentOnChain))
                    .finalityScore(isL1CommitmentOnChain)
                    .merkleProof(convertMerkleProof(proof, maybeTransactionDetails))
                    .build());

        }).orElseGet(() -> {
            log.info("Merkle proof not found yet for voteId:{}", vote.getId());

            return Either.<Problem, VoteReceipt>right(VoteReceipt.builder()
                    .id(vote.getId())
                    .event(event.id())
                    .category(category.id())
                    .proposal(proposalIdOrName)
                    .coseSignature(vote.getCoseSignature())
                    .cosePublicKey(vote.getCosePublicKey())
                    .votedAtSlot(Long.valueOf(vote.getVotedAtSlot()).toString())
                    .voterStakingAddress(vote.getVoterStakingAddress())
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
                        .type(VoteReceipt.MerkleProofType.Left)
                        .hash(encodeHexString(pl.hash()))
                        .build();
            }

            if (item instanceof ProofItem.Right pr) {
                return VoteReceipt.MerkleProofItem.builder()
                        .type(VoteReceipt.MerkleProofType.Right)
                        .hash(encodeHexString(pr.hash()))
                        .build();
            }

            throw new RuntimeException("Unknown proof item type:" + item.getClass().getName());
        }).toList();
    }

}
