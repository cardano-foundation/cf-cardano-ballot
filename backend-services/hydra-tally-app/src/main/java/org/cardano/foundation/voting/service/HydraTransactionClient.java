package org.cardano.foundation.voting.service;

import com.bloxbean.cardano.client.transaction.util.TransactionUtil;
import com.bloxbean.cardano.client.util.HexUtil;
import jakarta.annotation.Nullable;
import lombok.extern.slf4j.Slf4j;
import org.cardano.foundation.voting.domain.TxGlobalRequest;
import org.cardano.foundation.voting.domain.TxLocalRequest;
import org.cardano.foundation.voting.domain.TxResult;
import org.cardanofoundation.hydra.client.HydraClientOptions;
import org.cardanofoundation.hydra.client.HydraQueryEventListener;
import org.cardanofoundation.hydra.client.HydraStateEventListener;
import org.cardanofoundation.hydra.client.HydraWSClient;
import org.cardanofoundation.hydra.core.model.HydraState;
import org.cardanofoundation.hydra.core.model.Request;
import org.cardanofoundation.hydra.core.model.Transaction;
import org.cardanofoundation.hydra.core.model.UTXO;
import org.cardanofoundation.hydra.core.model.query.response.*;
import org.cardanofoundation.hydra.core.store.UTxOStore;
import reactor.core.publisher.Mono;
import reactor.core.publisher.MonoSink;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

import static org.cardanofoundation.hydra.core.model.HydraState.*;

@Slf4j
public class HydraTransactionClient extends HydraQueryEventListener.Stub implements HydraStateEventListener {

    private UTxOStore uTxOStore;

    @Nullable private HydraWSClient hydraWSClient;

    private final HydraClientOptions hydraClientOptions;

    public HydraTransactionClient(UTxOStore uTxOStore,
                                  HydraClientOptions hydraClientOptions) {
        this.uTxOStore = uTxOStore;
        this.hydraClientOptions = hydraClientOptions;
    }

    private Map<String, List<MonoSink>> monoSinkMap = new ConcurrentHashMap<>();

    private void initWSClient() {
        if (this.hydraWSClient == null) {
            this.hydraWSClient = new HydraWSClient(hydraClientOptions);
            hydraWSClient.addHydraStateEventListener(this);
            hydraWSClient.addHydraQueryEventListener(this);
            this.monoSinkMap = new ConcurrentHashMap<>();
        }
    }

    private void destroyWSClient() {
        hydraWSClient.removeHydraStateEventListener(this);
        hydraWSClient.removeHydraStateEventListener(this);
        this.monoSinkMap = new ConcurrentHashMap<>();

        this.hydraWSClient = null;
    }

    @Override
    public void onResponse(Response response) {
        log.info("Tag:{}, seq:{}", response.getTag(), response.getSeq());

        if (response instanceof HeadIsOpenResponse ho) {
            // we get initial UTxOs here as well
            var utxo = ho.getUtxo();
            uTxOStore.storeLatestUtxO(utxo);
        }

        if (response instanceof SnapshotConfirmed sc) {
            Map<String, UTXO> utxo = sc.getSnapshot().getUtxo();
            uTxOStore.storeLatestUtxO(utxo);

            for (Transaction trx : sc.getSnapshot().getConfirmedTransactions()) {
                TxResult txResult = new TxResult(trx.getId(), trx.getIsValid());

                TxGlobalRequest txGlobalRequest = TxGlobalRequest.of(trx.getId());
                applyMonoSuccess(txGlobalRequest.key(), txResult);
            }
        }

        if (response instanceof GreetingsResponse gr) {
            var utxo = gr.getSnapshotUtxo();
            uTxOStore.storeLatestUtxO(utxo);
        }

        if (response instanceof TxValidResponse txResponse) {
            String txId = txResponse.getTransaction().getId();
            TxResult txResult = new TxResult(txId, true);

            applyMonoSuccess(TxLocalRequest.of(txId).toString(), txResult);
        }
        if (response instanceof TxInvalidResponse txResponse) {
            String txId = txResponse.getTransaction().getId();
            String reason = txResponse.getValidationError().getReason();
            TxResult txResult = new TxResult(txId, true, reason);

            applyMonoSuccess(TxLocalRequest.of(txId).key(), txResult);
        }
    }

    @Override
    public void onStateChanged(HydraState prevState, HydraState newState) {
        log.info("On StateChange Prev State: {}, new state:{}", prevState, newState);
    }

    public HydraState getHydraState() {
        if (hydraWSClient == null) {
            return Unknown;
        }

        return hydraWSClient.getHydraState();
    }

    public Mono<TxResult> submitTx(byte[] cborTx) {
        return Mono.create(monoSink -> {
            String txHash = TransactionUtil.getTxHash(cborTx);
            storeMonoSinkReference(TxLocalRequest.of(txHash).key(), monoSink);
            hydraWSClient.submitTx(HexUtil.encodeHexString(cborTx));
        });
    }

    public Mono<TxResult> submitTxFullConfirmation(byte[] cborTx) {
        return Mono.create(monoSink -> {
            String txHash = TransactionUtil.getTxHash(cborTx);
            log.info("Submitting tx:" + txHash);

            storeMonoSinkReference(TxGlobalRequest.of(txHash).key(), monoSink);
            hydraWSClient.submitTx(HexUtil.encodeHexString(cborTx));
        });
    }

    protected void storeMonoSinkReference(String key, MonoSink monoSink) {
        monoSinkMap.computeIfAbsent(key, k -> {
            var list = new ArrayList<MonoSink>();
            list.add(monoSink);

            return list;
        });
    }

    protected <T extends Request> void applyMonoSuccess(String key, Object result) {
        List<MonoSink> monoSinks = monoSinkMap.remove(key);
        if (monoSinks == null) {
            return;
        }

        monoSinks.forEach(monoSink -> monoSink.success(result));
    }

    protected <T extends Request> void applyMonoSuccess(String key) {
        List<MonoSink> monoSinks = monoSinkMap.remove(key);
        if (monoSinks == null) {
            return;
        }

        monoSinks.forEach(MonoSink::success);
    }

    protected <T extends Request> void applyMonoError(String key, Object result) {
        List<MonoSink> monoSinks = monoSinkMap.remove(key);
        if (monoSinks == null) {
            return;
        }

        monoSinks.forEach(monoSink -> monoSink.error(new RuntimeException(String.valueOf(result))));
    }

    public boolean openConnection(int unit, TimeUnit timeUnit) throws InterruptedException {
        initWSClient();

        if (!hydraWSClient.isOpen()) {
            hydraWSClient.connectBlocking(unit, timeUnit);

            return true;
        }

        return false;
    }

    public boolean closeConnection() throws InterruptedException {
        if (hydraWSClient.isOpen()) {
            hydraWSClient.closeBlocking();
            destroyWSClient();

            return true;
        }

        return false;
    }

    public boolean abortHead() {
        if (hydraWSClient.getHydraState() == Initializing) {
            hydraWSClient.abort();

            return true;
        }

        return false;
    }

    public boolean initHead() {
        if (hydraWSClient.getHydraState() == Idle) {
            hydraWSClient.init();

            return true;
        }

        return false;
    }

    public boolean commitEmptyToTheHead() {
        if (hydraWSClient.getHydraState() == Initializing) {
            hydraWSClient.commit();

            return true;
        }

        return false;
    }


    public boolean commitFundsToTheHead(String cardanoCommitAddress,
                                        String cardanoCommitUtxo,
                                        long cardanoCommitAmount) {
        if (hydraWSClient.getHydraState() == Initializing) {
            var utxo = new UTXO();
            utxo.setAddress(cardanoCommitAddress);
            utxo.setValue(Map.of("lovelace", BigInteger.valueOf(cardanoCommitAmount)));

            hydraWSClient.commit(cardanoCommitUtxo, utxo);

            return true;
        }

        return false;
    }

    public boolean fanOutHead() {
        if (hydraWSClient.getHydraState() == FanoutPossible) {
            hydraWSClient.fanOut();

            return true;
        }

        return false;
    }

    public boolean closeHead() {
        if (hydraWSClient.getHydraState() == Open) {
            hydraWSClient.closeHead();

            return true;
        }

        return false;
    }

}