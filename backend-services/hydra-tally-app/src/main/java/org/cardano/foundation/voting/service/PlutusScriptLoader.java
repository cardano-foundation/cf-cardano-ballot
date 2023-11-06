package org.cardano.foundation.voting.service;

import com.bloxbean.cardano.aiken.tx.evaluator.InitialBudgetConfig;
import com.bloxbean.cardano.aiken.tx.evaluator.SlotConfig;
import com.bloxbean.cardano.aiken.tx.evaluator.TxEvaluator;
import com.bloxbean.cardano.client.api.exception.ApiRuntimeException;
import com.bloxbean.cardano.client.api.model.ProtocolParams;
import com.bloxbean.cardano.client.api.model.Utxo;
import com.bloxbean.cardano.client.common.model.Network;
import com.bloxbean.cardano.client.plutus.spec.*;
import com.bloxbean.cardano.client.transaction.spec.Transaction;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.annotation.PostConstruct;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.cardano.foundation.voting.client.ChainFollowerClient;
import org.cardano.foundation.voting.domain.TallyType;
import org.cardano.foundation.voting.domain.VotingEventType;
import org.cardanofoundation.hydra.core.HydraException;
import org.cardanofoundation.hydra.core.utils.HexUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.List;
import java.util.Set;

import static com.bloxbean.cardano.aiken.AikenScriptUtil.applyParamToScript;
import static com.bloxbean.cardano.client.address.AddressProvider.getEntAddress;
import static com.bloxbean.cardano.client.api.util.CostModelUtil.getCostModelFromProtocolParams;
import static com.bloxbean.cardano.client.crypto.Blake2bUtil.blake2bHash224;
import static com.bloxbean.cardano.client.plutus.blueprint.PlutusBlueprintUtil.getPlutusScriptFromCompiledCode;
import static com.bloxbean.cardano.client.plutus.blueprint.model.PlutusVersion.v2;
import static com.bloxbean.cardano.client.plutus.spec.Language.PLUTUS_V2;
import static java.nio.charset.StandardCharsets.UTF_8;

@Component
@Slf4j
public class PlutusScriptLoader {

    @Autowired
    private Network network;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private ResourceLoader resourceLoader;

    @Autowired
    private ChainFollowerClient chainFollowerClient;

    @Value("${ballot.event.id}")
    private String eventId;

    @Value("${ballot.tally.name}")
    private String tallyName;

    private String parametrisedCompiledTemplate;

    @Getter
    private List<String> verificationKeys;

    @Getter
    private String organisers;

    @Getter
    private VotingEventType votingEventType;

    @PostConstruct
    public void init() throws IOException {
        var eventDetailsE = chainFollowerClient.getEventDetails(eventId);

        if (eventDetailsE.isEmpty()) {
            var issue = eventDetailsE.swap().get();

            throw new HydraException("Error while retrieving event, issue:" + issue);
        }

        var eventDetailsResponseM = eventDetailsE.get();

        if (eventDetailsResponseM.isEmpty()) {
            throw new HydraException("Event not found on ledger follower service, eventId:" + eventId);
        }

        var eventDetailsResponse = eventDetailsResponseM.orElseThrow();

        var tally = eventDetailsResponse.findTallyByName(tallyName)
                .orElseThrow(() -> new HydraException("Tally not found on ledger follower service, tallyName:" + tallyName));

        if (tally.type() != TallyType.HYDRA) {
            throw new HydraException("Tally type is not HYDRA, tallyName:" + tallyName);
        }

        var hydraTally = (ChainFollowerClient.HydraTallyConfig) tally.config();

        this.parametrisedCompiledTemplate = hydraTally.compiledScript();
        this.organisers = eventDetailsResponse.organisers();
        var compiledScriptHash = hydraTally.compiledScriptHash();

        log.info("Plutus contract hash: {}", compiledScriptHash);

        this.verificationKeys = hydraTally.verificationKeys();

        log.info("Operator verification keys: {}", verificationKeys);
    }

    public String getContractAddress(PlutusScript plutusScript) {
        return getEntAddress(plutusScript, network).toBech32();
    }

    public PlutusScript getContract(String categoryId) {
        ListPlutusData.ListPlutusDataBuilder builder = ListPlutusData.builder();

        builder.plutusDataList(this.verificationKeys
                .stream()
                .map(HexUtils::decodeHexString)
                .map(blake224Hash -> (PlutusData) BytesPlutusData.of(blake224Hash))
                .toList());

        var verificationKeys = builder.build();

        val params = ListPlutusData.of(
                BytesPlutusData.of(blake2bHash224(tallyName.getBytes(UTF_8))),
                verificationKeys,
                BytesPlutusData.of(eventId),
                BytesPlutusData.of(organisers),
                BytesPlutusData.of(categoryId)
        );
        val compiledCode = applyParamToScript(params, parametrisedCompiledTemplate);

        return getPlutusScriptFromCompiledCode(compiledCode, v2);
    }

    public static List<Redeemer> evaluateExUnits(Transaction txn,
                                                 Set<Utxo> utxos,
                                                 ProtocolParams protocolParams) {
        val txMem = Long.valueOf(protocolParams.getMaxTxExMem());
        val txCpu = Long.valueOf(protocolParams.getMaxTxExSteps());

        val slot_length = 1000;
        val zero_slot = 0;
        val zero_time = 1660003200000L;

        try (val slotConfig = new SlotConfig(slot_length, zero_slot, zero_time);
            val initialBudgetConfig = new InitialBudgetConfig(txMem, txCpu)) {
            val txEvaluator = new TxEvaluator(slotConfig, initialBudgetConfig);
            val costMdls = new CostMdls();

            val costModelFromProtocolParams = getCostModelFromProtocolParams(protocolParams, PLUTUS_V2);
            costModelFromProtocolParams.ifPresent(costMdls::add);

            return txEvaluator.evaluateTx(txn, utxos, costMdls);
        } catch (IOException e) {
            throw new ApiRuntimeException(e);
        }
    }

}
