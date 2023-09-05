package org.cardano.foundation.voting.shell;

import com.bloxbean.cardano.client.util.HexUtil;
import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.jwk.gen.OctetKeyPairGenerator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.springframework.shell.standard.ShellComponent;
import org.springframework.shell.standard.ShellMethod;

import java.util.UUID;

import static com.nimbusds.jose.jwk.Curve.Ed25519;
import static java.nio.charset.StandardCharsets.UTF_8;

@ShellComponent
@Slf4j
@RequiredArgsConstructor
public class AdminKeysGenerationCommands {

    @ShellMethod(key = "generate-jwt-keys", value = "Generates JWT keys.")
    public String generateJWTAdminKeys() throws JOSEException {
        val key = new OctetKeyPairGenerator(Ed25519)
                .generate();

        val keyJson = key.toJSONString();

        log.info("JWT key as json:" + keyJson);

        String keyHex = HexUtil.encodeHexString(keyJson.getBytes(UTF_8));

        log.info("JWT key as hex:" + keyHex);

        return "Created JWT key (hex): " + keyHex;
    }

    @ShellMethod(key = "generate-salt", value = "Generates salt.")
    public String generateSalt() {
        String randomUUID = UUID.randomUUID().toString();

        log.info("UUID: " + randomUUID);

        var uuidSanitized = randomUUID.replace("-", "");
        log.info("UUID (sanitized) (HEX): " + uuidSanitized);

        return "Created salt key (hex): " + uuidSanitized;
    }

}