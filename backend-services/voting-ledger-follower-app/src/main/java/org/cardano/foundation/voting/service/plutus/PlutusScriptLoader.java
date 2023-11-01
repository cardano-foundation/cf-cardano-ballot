package org.cardano.foundation.voting.service.plutus;

import com.bloxbean.cardano.client.common.model.Network;
import com.bloxbean.cardano.client.crypto.Blake2bUtil;
import com.bloxbean.cardano.client.plutus.spec.BytesPlutusData;
import com.bloxbean.cardano.client.plutus.spec.ListPlutusData;
import com.bloxbean.cardano.client.plutus.spec.PlutusData;
import com.bloxbean.cardano.client.plutus.spec.PlutusScript;
import com.bloxbean.cardano.client.util.HexUtil;
import lombok.Builder;
import lombok.extern.slf4j.Slf4j;
import lombok.val;

import java.nio.charset.StandardCharsets;
import java.util.List;

import static com.bloxbean.cardano.aiken.AikenScriptUtil.applyParamToScript;
import static com.bloxbean.cardano.client.address.AddressProvider.getEntAddress;
import static com.bloxbean.cardano.client.crypto.Blake2bUtil.blake2bHash224;
import static com.bloxbean.cardano.client.plutus.blueprint.PlutusBlueprintUtil.getPlutusScriptFromCompiledCode;
import static com.bloxbean.cardano.client.plutus.blueprint.model.PlutusVersion.v2;
import static java.nio.charset.StandardCharsets.UTF_8;

@Slf4j
@Builder
public class PlutusScriptLoader {

    private String parametrisedCompiledTemplate;

    private List<String> verificationKeys;

    private String tallyName;

    private String eventId;

    private String categoryId;

    private String organiser;

    public PlutusScript getCompiledScript() {
        ListPlutusData.ListPlutusDataBuilder builder = ListPlutusData.builder();

        builder.plutusDataList(this.verificationKeys
                .stream()
                .map(HexUtil::decodeHexString)
                .map(blake224Hash -> (PlutusData) BytesPlutusData.of(blake224Hash))
                .toList());

        ListPlutusData verificationKeysPlutusData = builder.build();

        val params = ListPlutusData.of(
                BytesPlutusData.of(blake2bHash224(tallyName.getBytes(UTF_8))),
                verificationKeysPlutusData,
                BytesPlutusData.of(eventId),
                BytesPlutusData.of(organiser),
                BytesPlutusData.of(categoryId)
        );
        val compiledCode = applyParamToScript(params, parametrisedCompiledTemplate);

        return getPlutusScriptFromCompiledCode(compiledCode, v2);
    }

    public String getContractAddress(PlutusScript plutusScript, Network network) {
        return getEntAddress(plutusScript, network).toBech32();
    }

}
