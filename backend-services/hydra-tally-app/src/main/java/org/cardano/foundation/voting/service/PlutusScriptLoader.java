package org.cardano.foundation.voting.service;

import com.bloxbean.cardano.aiken.tx.evaluator.InitialBudgetConfig;
import com.bloxbean.cardano.aiken.tx.evaluator.SlotConfig;
import com.bloxbean.cardano.aiken.tx.evaluator.TxEvaluator;
import com.bloxbean.cardano.client.api.exception.ApiRuntimeException;
import com.bloxbean.cardano.client.api.model.ProtocolParams;
import com.bloxbean.cardano.client.api.model.Utxo;
import com.bloxbean.cardano.client.common.model.Network;
import com.bloxbean.cardano.client.crypto.KeyGenUtil;
import com.bloxbean.cardano.client.crypto.VerificationKey;
import com.bloxbean.cardano.client.plutus.spec.*;
import com.bloxbean.cardano.client.transaction.spec.Transaction;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
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
import static com.bloxbean.cardano.client.plutus.blueprint.PlutusBlueprintUtil.getPlutusScriptFromCompiledCode;
import static com.bloxbean.cardano.client.plutus.blueprint.model.PlutusVersion.v2;
import static com.bloxbean.cardano.client.plutus.spec.Language.PLUTUS_V2;

@Component
@Slf4j
public class PlutusScriptLoader {

    @Autowired
    private Network network;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private ResourceLoader resourceLoader;

    @Value("${plutus.contract.path}")
    private String plutusCodePath;

    @Value("${operators.verification.keys}")
    private List<String> operatorVerificationKeys;

    private String parametrisedCompiledTemplate;

    @PostConstruct
    public void init() throws IOException {
        if (operatorVerificationKeys.isEmpty()) {
            throw new RuntimeException("No operator verification keys configured!");
        }

        log.info("Operator verification keys: {}", operatorVerificationKeys);

        var plutusFileAsString = resourceLoader.getResource(plutusCodePath)
                .getInputStream();

        var validatorsNode =  ((ArrayNode) objectMapper.readTree(plutusFileAsString).get("validators"));
        this.parametrisedCompiledTemplate = validatorsNode.get(0).get("compiledCode").asText();
        String hash = validatorsNode.get(0).get("hash").asText();

        log.info("Operator verification keys: {}", operatorVerificationKeys);

        log.info("Contract Hash: {}", hash);
    }

    public String getContractAddress(PlutusScript plutusScript) {
        return getEntAddress(plutusScript, network).toBech32();
    }

    public PlutusScript getContract(String eventId,
                                    String organiser,
                                    String categoryId) {

        ListPlutusData.ListPlutusDataBuilder builder = ListPlutusData.builder();

        builder.plutusDataList(this.operatorVerificationKeys
                .stream()
                .map(VerificationKey::new)
                .map(KeyGenUtil::getKeyHash)
                .map(HexUtils::decodeHexString)
                .map(blake224Hash -> (PlutusData) BytesPlutusData.of(blake224Hash))
                .toList());

        val params = ListPlutusData.of(
                builder.build(),
                BytesPlutusData.of(eventId),
                BytesPlutusData.of(organiser),
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
