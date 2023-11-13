package org.cardano.foundation.voting.service.plutus;

import com.bloxbean.cardano.client.common.model.Network;
import com.bloxbean.cardano.client.plutus.spec.BytesPlutusData;
import com.bloxbean.cardano.client.plutus.spec.ListPlutusData;
import com.bloxbean.cardano.client.plutus.spec.PlutusData;
import com.bloxbean.cardano.client.plutus.spec.PlutusScript;
import com.bloxbean.cardano.client.util.HexUtil;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.cardano.foundation.voting.domain.entity.Event;
import org.springframework.stereotype.Service;

import java.util.Optional;

import static com.bloxbean.cardano.aiken.AikenScriptUtil.applyParamToScript;
import static com.bloxbean.cardano.client.address.AddressProvider.getEntAddress;
import static com.bloxbean.cardano.client.crypto.Blake2bUtil.blake2bHash224;
import static com.bloxbean.cardano.client.plutus.blueprint.PlutusBlueprintUtil.getPlutusScriptFromCompiledCode;
import static com.bloxbean.cardano.client.plutus.blueprint.model.PlutusVersion.v2;
import static java.nio.charset.StandardCharsets.UTF_8;
import static org.cardano.foundation.voting.domain.entity.Tally.TallyType.HYDRA;

@Slf4j
@Service
public class PlutusScriptLoader {

    public Optional<PlutusScript> compileScriptBy(Event event,
                                                String category,
                                                String tallyName) {
        ListPlutusData.ListPlutusDataBuilder builder = ListPlutusData.builder();

        var tallyM = event.getTallies()
                .stream()
                .filter(tally -> tally.getName().equals(tallyName))
                .filter(tally -> tally.getType() == HYDRA)
                .findFirst();

        if (tallyM.isEmpty()) {
            return Optional.empty();
        }

        var tally = tallyM.orElseThrow();
        var hydraTallyConfig = tally.getHydraTallyConfig();
        var verificationKeys = hydraTallyConfig.getVerificationKeysHashesAsList();

        builder.plutusDataList(verificationKeys
                .stream()
                .map(HexUtil::decodeHexString)
                .map(blake224Hash -> (PlutusData) BytesPlutusData.of(blake224Hash))
                .toList());

        ListPlutusData verificationKeysPlutusData = builder.build();

        val params = ListPlutusData.of(
                BytesPlutusData.of(blake2bHash224(tallyName.getBytes(UTF_8))),
                verificationKeysPlutusData,
                BytesPlutusData.of(event.getId()),
                BytesPlutusData.of(event.getOrganisers()),
                BytesPlutusData.of(category)
        );
        val compiledCode = applyParamToScript(params, hydraTallyConfig.getCompiledScript());

        return Optional.of(getPlutusScriptFromCompiledCode(compiledCode, v2));
    }

    public String getContractAddress(PlutusScript plutusScript, Network network) {
        return getEntAddress(plutusScript, network).toBech32();
    }

}
