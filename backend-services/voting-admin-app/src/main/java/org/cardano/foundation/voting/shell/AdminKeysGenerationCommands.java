package org.cardano.foundation.voting.shell;

import com.bloxbean.cardano.client.account.Account;
import com.bloxbean.cardano.client.common.model.Network;
import com.bloxbean.cardano.client.common.model.Networks;
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

    private final Network network;

    @ShellMethod(key = "admin-generate-new-account", value = "Generates new shelley address and staking key.")
    public String generateNewAccount() {
        val newAccount = new Account(network);

        var sb = new StringBuilder();

        sb
        .append("Account created.").append("\n").append("\n")
        .append("Network: ").append(network.toString()).append("\n")
        .append("Address: ").append(newAccount.baseAddress()).append("\n")
        .append("StakeAddress: ").append(newAccount.stakeAddress()).append("\n")
        .append("Mnemonic: ").append(newAccount.mnemonic()).append("\n")
        .append("\n");

        return sb.toString();
    }

    @ShellMethod(key = "admin-generate-jwt-keys", value = "Generates JWT keys.")
    public String generateJWTAdminKeys() throws JOSEException {
        val key = new OctetKeyPairGenerator(Ed25519)
                .generate();

        val keyJson = key.toJSONString();

        var sb = new StringBuilder();

        sb.append("JWT admin keys generated.").append("\n").append("\n");

        sb.append("JWT key (json): ").append(key.toJSONString()).append("\n");

        sb.append("JWT key (hex): ").append(HexUtil.encodeHexString(keyJson.getBytes(UTF_8))).append("\n");

        return sb.toString();
    }

    @ShellMethod(key = "admin-generate-salt", value = "Generates salt.")
    public String generateSalt() {
        String randomUUID = UUID.randomUUID().toString();

        var uuidSanitized = randomUUID.replace("-", "");

        var sb = new StringBuilder();

        sb.append("Salt generated.").append("\n").append("\n");

        sb.append("Salt key (hex): ").append(uuidSanitized).append("\n");

        return sb.toString();
    }

}
