package org.cardano.foundation.voting.service.plutus;

import com.bloxbean.cardano.client.common.model.Network;
import com.bloxbean.cardano.client.crypto.KeyGenUtil;
import com.bloxbean.cardano.client.crypto.VerificationKey;
import com.bloxbean.cardano.client.plutus.spec.BytesPlutusData;
import com.bloxbean.cardano.client.plutus.spec.ListPlutusData;
import com.bloxbean.cardano.client.plutus.spec.PlutusData;
import com.bloxbean.cardano.client.plutus.spec.PlutusScript;
import com.bloxbean.cardano.client.util.HexUtil;
import lombok.Builder;
import lombok.extern.slf4j.Slf4j;
import lombok.val;

import java.util.List;

import static com.bloxbean.cardano.aiken.AikenScriptUtil.applyParamToScript;
import static com.bloxbean.cardano.client.address.AddressProvider.getEntAddress;
import static com.bloxbean.cardano.client.plutus.blueprint.PlutusBlueprintUtil.getPlutusScriptFromCompiledCode;
import static com.bloxbean.cardano.client.plutus.blueprint.model.PlutusVersion.v2;

@Slf4j
@Builder
public class PlutusScriptLoader {

    private String parametrisedCompiledTemplate;

    private String contractHash;

    private List<String> verificationKeys;

    private String eventId;

    private String categoryId;

    private String organiser;

    public PlutusScript getCompiledScript() {
        ListPlutusData.ListPlutusDataBuilder builder = ListPlutusData.builder();

        builder.plutusDataList(this.verificationKeys
                .stream()
                .map(VerificationKey::new)
                .map(KeyGenUtil::getKeyHash)
                .map(HexUtil::decodeHexString)
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

    public String getContractAddress(PlutusScript plutusScript, Network network) {
        return getEntAddress(plutusScript, network).toBech32();
    }

}
