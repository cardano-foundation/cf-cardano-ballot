package org.cardano.foundation.voting.config;

import com.bloxbean.cardano.client.util.HexUtil;
import com.nimbusds.jose.jwk.OctetKeyPair;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.text.ParseException;

@Configuration
public class JwtAuthConfig {

    @Value("${cardano.jwt.secret}")
    private String key;

    @Bean
    public OctetKeyPair jwtKeyPair() throws ParseException {
        var decoded = HexUtil.decodeHexString(key);

        return OctetKeyPair.parse(new String(decoded));
    }

}
