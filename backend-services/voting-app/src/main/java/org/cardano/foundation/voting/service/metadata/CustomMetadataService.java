package org.cardano.foundation.voting.service.metadata;

import com.bloxbean.cardano.client.account.Account;
import io.micrometer.core.annotation.Timed;
import io.vavr.control.Either;
import lombok.extern.slf4j.Slf4j;
import org.cardano.foundation.voting.domain.CardanoNetwork;
import org.cardano.foundation.voting.domain.TransactionMetadataLabelCbor;
import org.cardano.foundation.voting.domain.web3.SignedWeb3Request;
import org.cardano.foundation.voting.domain.web3.Web3Action;
import org.cardano.foundation.voting.repository.EventRepository;
import org.cardano.foundation.voting.service.address.StakeAddressVerificationService;
import org.cardano.foundation.voting.service.blockchain_state.BlockchainDataMetadataService;
import org.cardano.foundation.voting.service.blockchain_state.BlockchainDataTransactionDetailsService;
import org.cardano.foundation.voting.service.expire.ExpirationService;
import org.cardano.foundation.voting.service.json.JsonService;
import org.cardano.foundation.voting.utils.Enums;
import org.cardanofoundation.cip30.AddressFormat;
import org.cardanofoundation.cip30.CIP30Verifier;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.zalando.problem.Problem;

import java.util.List;

import static org.cardano.foundation.voting.domain.TransactionDetails.FinalityScore.FINAL;
import static org.cardano.foundation.voting.domain.TransactionDetails.FinalityScore.VERY_HIGH;
import static org.cardano.foundation.voting.domain.web3.Web3Action.FULL_METADATA_SCAN;
import static org.cardano.foundation.voting.utils.MoreNumber.isNumeric;
import static org.cardanofoundation.cip30.MessageFormat.TEXT;
import static org.cardanofoundation.cip30.ValidationError.UNKNOWN;
import static org.zalando.problem.Status.BAD_REQUEST;

@Service
@Slf4j
public class CustomMetadataService {

    private final static int PAGE_SIZE = 100;

    @Autowired
    private EventRepository eventRepository;

    @Value("${l1.transaction.metadata.label}")
    private long metadataLabel;

    @Autowired
    private BlockchainDataMetadataService blockchainDataMetadataService;

    @Autowired
    private BlockchainDataTransactionDetailsService blockchainDataTransactionDetailsService;

    @Autowired
    private CustomMetadataProcessor customMetadataProcessor;

    @Autowired
    private ExpirationService expirationService;

    @Autowired
    private StakeAddressVerificationService stakeAddressVerificationService;

    @Autowired
    private JsonService jsonService;

    @Autowired
    private CardanoNetwork cardanoNetwork;

    @Autowired
    @Qualifier("organiser_account")
    private Account organiserAccount;

    @Timed(value = "resource.metadata.full.scan.authed", percentiles = { 0.3, 0.5, 0.95 })
    public Either<Problem, Boolean> processAllMetadataEvents(SignedWeb3Request fullMetadataScanRequest) {
        var cip30Verifier = new CIP30Verifier(fullMetadataScanRequest.getCoseSignature(), fullMetadataScanRequest.getCosePublicKey());

        var cip30VerificationResult = cip30Verifier.verify();
        if (!cip30VerificationResult.isValid()) {
            var validationError = cip30VerificationResult.getValidationError().orElse(UNKNOWN);
            log.warn("CIP-30 data sign for full metadata scan verification failed, validationError:{}", validationError);

            return Either.left(
                    Problem.builder()
                            .withTitle("INVALID_CIP30_DATA_SIGNATURE")
                            .withDetail("Invalid full metadata scan signature!")
                            .withStatus(BAD_REQUEST)
                            .build()
            );
        }

        var jsonPayloadE = jsonService.decodeFullMetadataScanEnvelope(cip30VerificationResult.getMessage(TEXT));
        if (jsonPayloadE.isLeft()) {
            return Either.left(
                    Problem.builder()
                            .withTitle("INVALID_CIP30_DATA_SIGNATURE")
                            .withDetail("Invalid full metadata scan signature!")
                            .withStatus(BAD_REQUEST)
                            .build()
            );
        }
        var envelope = jsonPayloadE.get();
        var slotStr = envelope.getSlot();

        if (!isNumeric(slotStr)) {
            return Either.left(
                    Problem.builder()
                            .withTitle("INVALID_SLOT")
                            .withDetail("CIP-93 envelope slot is not numeric!")
                            .withStatus(BAD_REQUEST)
                            .build()
            );
        }

        if (expirationService.isSlotExpired(Long.parseLong(slotStr))) {
            return Either.left(
                    Problem.builder()
                            .withTitle("EXPIRED_SLOT")
                            .withDetail("CIP-93 envelope slot is expired!")
                            .withStatus(BAD_REQUEST)
                            .build()
            );
        }

        var actionText = envelope.getAction();
        var maybeAction = Enums.getIfPresent(Web3Action.class, actionText);
        if (maybeAction.isEmpty()) {
            log.warn("Unknown action, action:{}", actionText);

            return Either.left(Problem.builder()
                    .withTitle("ACTION_NOT_FOUND")
                    .withDetail("Action not found!")
                    .withStatus(BAD_REQUEST)
                    .build()
            );
        }

        var action = maybeAction.orElseThrow();
        if (action != FULL_METADATA_SCAN) {
            return Either.left(Problem.builder()
                    .withTitle("INVALID_ACTION")
                    .withDetail("Action is not FULL_METADATA_SCAN, action:" + action)
                    .withStatus(BAD_REQUEST)
                    .build()
            );
        }

        var maybeNetwork = Enums.getIfPresent(CardanoNetwork.class, envelope.getData().getNetwork());
        if (maybeNetwork.isEmpty()) {
            log.warn("Invalid network, network:{}", envelope.getData().getNetwork());

            return Either.left(Problem.builder()
                    .withTitle("INVALID_NETWORK")
                    .withDetail("Invalid network, supported networks:" + CardanoNetwork.supportedNetworks())
                    .withStatus(BAD_REQUEST)
                    .build());
        }

        var network = maybeNetwork.orElseThrow();

        if (network != cardanoNetwork) {
            log.warn("Invalid network, network:{}", envelope.getData().getNetwork());

            return Either.left(Problem.builder()
                    .withTitle("NETWORK_MISMATCH")
                    .withDetail("Invalid network, backed configured with network:" + cardanoNetwork + ", however request is with network:" + network)
                    .withStatus(BAD_REQUEST)
                    .build());
        }

        var maybeAddress = cip30VerificationResult.getAddress(AddressFormat.TEXT);
        if (maybeAddress.isEmpty()) {
            log.warn("Address not found in the signed data");

            return Either.left(
                    Problem.builder()
                            .withTitle("ADDRESS_NOT_FOUND")
                            .withDetail("Bech32 address not found in the signed data.")
                            .withStatus(BAD_REQUEST)
                            .build()
            );
        }
        var stakeAddress = maybeAddress.orElseThrow();

        var stakeAddressCheckE = stakeAddressVerificationService.checkIfAddressIsStakeAddress(stakeAddress);
        if (stakeAddressCheckE.isLeft()) {
            return Either.left(stakeAddressCheckE.getLeft());
        }

        var stakeAddressNetworkCheck = stakeAddressVerificationService.checkStakeAddressNetwork(stakeAddress);
        if (stakeAddressNetworkCheck.isLeft()) {
            return Either.left(stakeAddressNetworkCheck.getLeft());
        }

        boolean isOrganiser = organiserAccount.stakeAddress().equals(stakeAddress);
        if (!isOrganiser) {
            return Either.left(
                    Problem.builder()
                            .withTitle("NOT_ORGANISER_ACCOUNT")
                            .withDetail("CIP-30 envelope hasn't been signed by the organiser!")
                            .withStatus(BAD_REQUEST)
                            .build()
            );
        }

        return processAllMetadataEvents();
    }

    @Timed(value = "resource.metadata.full.scan", percentiles = { 0.3, 0.5, 0.95 })
    public Either<Problem, Boolean> processAllMetadataEvents() {
        boolean continueFetching = true;
        int page = 1;
        do {
            var transactionMetadataLabelCbors = blockchainDataMetadataService.fetchMetadataForLabel(String.valueOf(metadataLabel), PAGE_SIZE, page, FINAL);
            if (transactionMetadataLabelCbors.size() < PAGE_SIZE) {
                continueFetching = false;
            }
            page++;

            customMetadataProcessor.processMetadataEvents(transactionMetadataLabelCbors);

        } while (continueFetching);

        return Either.right(true);
    }

    @Timed(value = "resource.metadata.recent.scan", percentiles = { 0.3, 0.5, 0.95 })
    public void processRecentMetadataEvents() {
        log.info("processRecentMetadataEvents for metadata label {}", metadataLabel);
        var metadataLabelString = String.valueOf(metadataLabel);

        var transactionMetadataLabelCbors = blockchainDataMetadataService.fetchMetadataForLabel(metadataLabelString, PAGE_SIZE, 1, VERY_HIGH);

        if (transactionMetadataLabelCbors.isEmpty()) {
            log.info("No recent metadata events for metadata label {}", metadataLabel);
            return;
        }

        customMetadataProcessor.processMetadataEvents(transactionMetadataLabelCbors);

        log.info("processRecentMetadataEvents for metadata label {} completed.", metadataLabel);
    }

    @Timed(value = "resource.metadata.passthrough", percentiles = { 0.3, 0.5, 0.95 })
    public void processRecentMetadataEventsPassThrough(List<TransactionMetadataLabelCbor> transactionMetadataLabelCbors) {
        log.info("processRecentMetadataEvents for metadata label {}", metadataLabel);

        customMetadataProcessor.processMetadataEvents(transactionMetadataLabelCbors);

        log.info("processRecentMetadataEvents for metadata label {} completed.", metadataLabel);
    }

}
