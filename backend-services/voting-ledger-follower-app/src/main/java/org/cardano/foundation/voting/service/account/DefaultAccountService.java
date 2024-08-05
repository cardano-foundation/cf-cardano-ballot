package org.cardano.foundation.voting.service.account;

import io.vavr.control.Either;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.cardano.foundation.voting.domain.Account;
import org.cardano.foundation.voting.domain.ChainNetwork;
import org.cardano.foundation.voting.domain.WalletType;
import org.cardano.foundation.voting.service.reference_data.ReferenceDataService;
import org.cardano.foundation.voting.service.vote.VotingPowerService;
import org.cardano.foundation.voting.utils.Addresses;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.zalando.problem.Problem;

import java.util.List;

import static org.cardano.foundation.voting.domain.VotingEventType.BALANCE_BASED;
import static org.cardano.foundation.voting.domain.VotingEventType.STAKE_BASED;
import static org.zalando.problem.Status.BAD_REQUEST;
@Service
@Slf4j
@RequiredArgsConstructor
public class DefaultAccountService implements AccountService {

    private final ReferenceDataService referenceDataService;

    private final VotingPowerService votingPowerService;

    private final ChainNetwork network;

    @Override
    @Transactional(readOnly = true)
    public Either<Problem, Account> findAccount(String eventName, WalletType walletType, String walletId) {
        if (walletType == WalletType.KERI) {
            return Either.left(Problem.builder()
                    .withTitle("KERI_NOT_SUPPORTED")
                    .withDetail("Only Cardano wallet type supported for account / balance queries is supported.")
                    .withStatus(BAD_REQUEST)
                    .build()
            );
        }

        val eventM = referenceDataService.findValidEventByName(eventName);
        if (eventM.isEmpty()) {
            log.warn("Unrecognised event, eventName:{}", eventName);

            return Either.left(Problem.builder()
                    .withTitle("UNRECOGNISED_EVENT")
                    .withDetail("Unrecognised event, eventName:" + eventName)
                    .withStatus(BAD_REQUEST)
                    .build());
        }
        val event = eventM.orElseThrow();


        val walletIdCheckE = Addresses.checkWalletId(network, walletType, walletId);
        if (walletIdCheckE.isEmpty()) {
            return Either.left(walletIdCheckE.getLeft());
        }

        if (!List.of(STAKE_BASED, BALANCE_BASED).contains(event.getVotingEventType())) {
            return Either.left(Problem.builder()
                    .withTitle("ONLY_STAKE_AND_BALANCE_BASED_EVENTS_SUPPORTED")
                    .withDetail("Only stake and balance based events are supported, event:" + event.getId())
                    .withStatus(BAD_REQUEST)
                    .build());
        }

        val amountE = votingPowerService.getVotingPower(event, walletId);

        if (amountE.isEmpty()) {
            return Either.left(amountE.getLeft());
        }

        val amount = amountE.get();

        return Either.right(Account.builder()
                .walletType(walletType)
                .walletId(walletId)
                .network(network)
                .epochNo(event.getSnapshotEpoch().orElseThrow())
                .votingPower(String.valueOf(amount))
                .votingPowerAsset(event.getVotingPowerAsset().orElseThrow())
                .build()
        );
    }

}
