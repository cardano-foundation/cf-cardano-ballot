package org.cardano.foundation.voting.api.tests;

import io.restassured.RestAssured;
import org.cardano.foundation.voting.domain.Role;
import org.cardano.foundation.voting.service.auth.web3.Web3AuthenticationToken;
import org.cardano.foundation.voting.service.auth.web3.Web3Details;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@ActiveProfiles("test")
public class VoteTests {

    @LocalServerPort
    private int serverPort;

    @BeforeAll
    public void setUp() {
        RestAssured.port = serverPort;
        RestAssured.baseURI = "http://localhost";
    }

    @Test
    public void testCastVote() {
        List<SimpleGrantedAuthority> authorities = List.of(new SimpleGrantedAuthority(Role.VOTER.name()));
        //Web3Details web3Details = new Web3Details("0x1234567890", "0x1234567890", "0x1234567890", "0x1234567890");
        //new Web3AuthenticationToken(web3Details, authorities);
    }
}
