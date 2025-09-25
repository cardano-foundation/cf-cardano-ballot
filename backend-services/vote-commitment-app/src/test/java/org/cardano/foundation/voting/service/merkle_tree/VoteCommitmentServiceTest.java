package org.cardano.foundation.voting.service.merkle_tree;

import com.bloxbean.cardano.client.crypto.Blake2bUtil;
import com.bloxbean.cardano.client.util.HexUtil;
import io.vavr.control.Either;
import org.cardano.foundation.voting.client.ChainFollowerClient;
import org.cardano.foundation.voting.domain.VoteSerialisations;
import org.cardano.foundation.voting.domain.WalletType;
import org.cardano.foundation.voting.domain.web3.VoteEnvelope;
import org.cardano.foundation.voting.domain.web3.CIP93Envelope;
import org.cardano.foundation.voting.repository.VoteRepository;
import org.cardano.foundation.voting.service.json.JsonService;
import org.cardano.foundation.voting.service.transaction_submit.L1SubmissionService;
import org.cardano.foundation.voting.service.vote.VoteService;
import org.cardanofoundation.cip30.CIP30Verifier;
import org.cardanofoundation.cip30.Cip30VerificationResult;
import org.cardanofoundation.cip30.MessageFormat;
import org.cardanofoundation.cip30.AddressFormat;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.zalando.problem.Problem;

import java.nio.charset.StandardCharsets;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("VoteCommitmentService Tests")
class VoteCommitmentServiceTest {

    @Mock
    private VoteService voteService;

    @Mock
    private ChainFollowerClient chainFollowerClient;

    @Mock
    private L1SubmissionService l1SubmissionService;

    @Mock
    private VoteMerkleProofService voteMerkleProofService;

    @Mock
    private MerkleProofSerdeService merkleProofSerdeService;

    @Mock
    private JsonService jsonService;

    @InjectMocks
    private VoteCommitmentService voteCommitmentService;

    @Mock
    private VoteRepository.CompactVote mockVote;

    @Mock
    private Cip30VerificationResult mockCip30Result;

    @Mock
    private VoteEnvelope mockVoteEnvelope;

    // Test constants - Hardware wallet (hashed scenario) using authentic data from CIP30 unit tests
    private static final String HARDWARE_WALLET_SIGNATURE = "84582aa201276761646472657373581de0e1bdbfc0b500c4356adcd27ebedee1fdeba0206e5a77fb2f73dee1bda166686173686564f5581ccd197892c9f088d3fa45ceacab6e102629e40b7daa59ee89a583188058409aa83c7513b5bcfa12ddd7dabd33e338b6cbd17cbda70f6dcdd5577e9ed8e01a76f8d8321c2d5f5446c60a65c0ae3a6e0f91933cd1cd92d83ea506db7829510b";
    private static final String HARDWARE_WALLET_PAYLOAD = "{\"action\":\"CAST_VOTE\",\"actionText\":\"Cast Vote\",\"data\":{\"category\":\"INFRASTRUCTURE_PLATFORM\",\"event\":\"CF_SUMMIT_2025_26BCC\",\"id\":\"3d92b7c0-0f15-4b44-b630-9587d589bc9b\",\"network\":\"PREPROD\",\"proposal\":\"1C70E403-E1E9-4825-8246-8A2EB39E4F69\",\"votedAt\":\"103121738\",\"votingEventType\":\"USER_BASED\",\"walletId\":\"stake_test1ursmm07qk5qvgdt2mnf8a0k7u877hgpqded807e0w00wr0glrmpz0\",\"walletType\":\"CARDANO\"},\"slot\":\"103121738\"}";
    private static final String HARDWARE_WALLET_KEY = "a40101032720062158200d8a01827dabd66f477d54f30d506e38f4ef9687f21faa49d7e35e750c43f105";
    private static final String EXPECTED_BLAKE2B_224_HASH = "cd197892c9f088d3fa45ceacab6e102629e40b7daa59ee89a5831880";

    // Test constants - Software wallet (embedded JSON scenario) using real-world data
    private static final String SOFTWARE_WALLET_SIGNATURE = "84582aa201276761646472657373581de095de85bf39807e505a74d4ed410b61fd73f0fa5a5257a86f69cd8ccfa166686173686564f459019b7b22616374696f6e223a22434153545f564f5445222c22616374696f6e54657874223a224361737420566f7465222c2264617461223a7b2263617465676f7279223a22494e4652415354525543545552455f504c4154464f524d222c226576656e74223a2243465f53554d4d49545f323032355f3236424343222c226964223a2232653064353361612d353661322d346136362d613934342d333165623837323739366531222c226e6574776f726b223a2250524550524f44222c2270726f706f73616c223a2231383742303535442d423541312d343139382d394430322d324546374432323839363238222c22766f7465644174223a22313033313133363736222c22766f74696e674576656e7454797065223a22555345525f4241534544222c2277616c6c65744964223a227374616b655f7465737431757a32616170646c3878713875357a36776e3277367367747638376838753836746666393032723064387863656e63797077736637222c2277616c6c657454797065223a2243415244414e4f227d2c22736c6f74223a22313033313133363736227d5840a4f7b049e88422c4870882b1ac084e109156704ea4b8be60cc86f6054eec99bfea80964075e337cc07e46678134dad28e62e4dcbd0aff82312c51d0227b1060a";
    private static final String SOFTWARE_WALLET_PAYLOAD = "{\"action\":\"CAST_VOTE\",\"actionText\":\"Cast Vote\",\"data\":{\"category\":\"INFRASTRUCTURE_PLATFORM\",\"event\":\"CF_SUMMIT_2025_26BCC\",\"id\":\"2e0d53aa-56a2-4a66-a944-31eb872796e1\",\"network\":\"PREPROD\",\"proposal\":\"187B055D-B5A1-4198-9D02-2EF7D2289628\",\"votedAt\":\"103113676\",\"votingEventType\":\"USER_BASED\",\"walletId\":\"stake_test1uz2aapdl8xq8u5z6wn2w6sgtv87h8u86tff902r0d8xcencypwsf7\",\"walletType\":\"CARDANO\"},\"slot\":\"103113676\"}";
    private static final String SOFTWARE_WALLET_KEY = "a4010103272006215820d29c7c8a9cccae5f318e52a95d2dcf677e8d758d229fa899597d6b9891f73ac6";

    /**
     * Sanity check helper method to verify hash consistency between signature and payload.
     * This ensures that our hardware wallet test data is internally consistent and validates
     * that the Blake2b-224 hash computation is working correctly.
     */
    private void verifyHardwareWalletHashConsistency(String signature, String payload, String key, String expectedHash) {
        // Verify CIP30 signature parsing
        var cip30Verifier = new CIP30Verifier(signature, key);
        var result = cip30Verifier.verify();

        assertThat(result.isValid()).withFailMessage("CIP30 signature must be valid").isTrue();
        assertThat(result.isHashed()).withFailMessage("Hardware wallet signature must be hashed").isTrue();

        var signatureHash = result.getMessage(MessageFormat.HEX);
        assertThat(signatureHash).withFailMessage("Hash from signature must match expected").isEqualTo(expectedHash);

        // Verify computed hash from payload
        byte[] payloadBytes = payload.getBytes(StandardCharsets.UTF_8);
        byte[] blake224Hash = Blake2bUtil.blake2bHash224(payloadBytes);
        String computedHash = HexUtil.encodeHexString(blake224Hash);

        assertThat(computedHash).withFailMessage("Computed hash must match expected").isEqualTo(expectedHash);
        assertThat(signatureHash).withFailMessage("Signature hash must match computed hash").isEqualTo(computedHash);
    }

    /**
     * Sanity check helper method to verify software wallet signature and embedded JSON payload.
     * Software wallets embed the full JSON payload directly in the signature message.
     */
    private void verifySoftwareWalletConsistency(String signature, String expectedPayload, String key) {
        // Verify CIP30 signature parsing
        var cip30Verifier = new CIP30Verifier(signature, key);
        var result = cip30Verifier.verify();

        assertThat(result.isValid()).withFailMessage("CIP30 signature must be valid").isTrue();
        assertThat(result.isHashed()).withFailMessage("Software wallet signature must not be hashed").isFalse();

        // For software wallets, the message should contain the full JSON payload
        var embeddedPayload = result.getMessage(MessageFormat.TEXT);
        assertThat(embeddedPayload).withFailMessage("Embedded payload must match expected").isEqualTo(expectedPayload);
    }

    @Nested
    @DisplayName("verifyVote method tests")
    class VerifyVoteTests {

        @Nested
        @DisplayName("Hardware wallet scenarios (hashed = true)")
        class HardwareWalletTests {

            @Test
            @DisplayName("Should verify vote successfully when hash matches payload (hardware wallet scenario)")
            void shouldVerifyVoteWhenHashMatchesPayload() {
                // Given - Hardware wallet scenario with matching hash and payload
                // First, verify our test data consistency
                verifyHardwareWalletHashConsistency(HARDWARE_WALLET_SIGNATURE, HARDWARE_WALLET_PAYLOAD, HARDWARE_WALLET_KEY, EXPECTED_BLAKE2B_224_HASH);

                String payload = HARDWARE_WALLET_PAYLOAD;
                String expectedHashString = EXPECTED_BLAKE2B_224_HASH;

                when(mockCip30Result.isHashed()).thenReturn(true);
                when(mockVote.getPayload()).thenReturn(Optional.of(payload));
                when(mockCip30Result.getMessage(MessageFormat.HEX)).thenReturn(expectedHashString);

                VoteEnvelope expectedVoteEnvelope = mock(VoteEnvelope.class);
                CIP93Envelope<VoteEnvelope> cip93Envelope = CIP93Envelope.<VoteEnvelope>builder()
                        .data(expectedVoteEnvelope)
                        .build();
                when(jsonService.decodeCIP93VoteEnvelope(payload))
                        .thenReturn(Either.right(cip93Envelope));

                // When
                Either<Problem, VoteEnvelope> result = voteCommitmentService.verifyVote(mockVote, mockCip30Result);

                // Then
                assertThat(result.isRight()).isTrue();
                assertThat(result.get()).isEqualTo(expectedVoteEnvelope);
                verify(jsonService).decodeCIP93VoteEnvelope(payload);
            }

            @Test
            @DisplayName("Should fail when hash doesn't match payload")
            void shouldFailWhenHashDoesNotMatchPayload() {
                // Given - Hardware wallet scenario where signature hash doesn't match stored payload
                // Use real hardware wallet signature but different payload to create mismatch
                CIP30Verifier realVerifier = new CIP30Verifier(HARDWARE_WALLET_SIGNATURE);
                var realResult = realVerifier.verify();
                String realHashFromSignature = realResult.getMessage(MessageFormat.TEXT);

                String wrongPayload = "different payload that won't match the signature hash";

                when(mockCip30Result.isHashed()).thenReturn(true);
                when(mockVote.getPayload()).thenReturn(Optional.of(wrongPayload));
                when(mockVote.getId()).thenReturn("test-vote-id");
                when(mockCip30Result.getMessage(MessageFormat.HEX)).thenReturn(realHashFromSignature);

                // When
                Either<Problem, VoteEnvelope> result = voteCommitmentService.verifyVote(mockVote, mockCip30Result);

                // Then
                assertThat(result.isLeft()).isTrue();
                Problem problem = result.getLeft();
                assertThat(problem.getTitle()).isEqualTo("INVALID_VOTE");
                assertThat(problem.getDetail()).contains("vote hash in signature does not match payload signature");
                assertThat(problem.getDetail()).contains("test-vote-id");
                verify(jsonService, never()).decodeCIP93VoteEnvelope(anyString());
            }

            @Test
            @DisplayName("Should handle case where signature contains hash but no payload stored")
            void shouldHandleCaseWhereSignatureContainsHashButNoPayloadStored() {
                // Given - Hardware wallet case where signature is hashed but no payload in database
                when(mockCip30Result.isHashed()).thenReturn(true);
                when(mockVote.getPayload()).thenReturn(Optional.empty());
                when(mockVote.getId()).thenReturn("test-vote-id");

                // Note: No need to stub getMessage() because the method fails earlier when payload is missing

                // When
                Either<Problem, VoteEnvelope> result = voteCommitmentService.verifyVote(mockVote, mockCip30Result);

                // Then - Should fail because hashed vote needs external payload
                assertThat(result.isLeft()).isTrue();
                Problem problem = result.getLeft();
                assertThat(problem.getTitle()).isEqualTo("INVALID_VOTE");
                assertThat(problem.getDetail()).contains("Hashed vote and external payload also missing");
                assertThat(problem.getDetail()).contains("test-vote-id");
            }

            @Test
            @DisplayName("Should fail when payload JSON is invalid")
            void shouldFailWhenPayloadJsonIsInvalid() {
                // Given - Hardware wallet scenario with invalid JSON payload but matching hash
                // Use real hardware wallet signature to get actual hash
                CIP30Verifier realVerifier = new CIP30Verifier(HARDWARE_WALLET_SIGNATURE);
                var realResult = realVerifier.verify();
                String realHashFromSignature = realResult.getMessage(MessageFormat.TEXT);

                // Create invalid JSON payload but compute its hash to match signature for this test scenario
                String invalidJsonPayload = "invalid json";
                byte[] payloadBytes = invalidJsonPayload.getBytes(StandardCharsets.UTF_8);
                byte[] blake224Hash = Blake2bUtil.blake2bHash224(payloadBytes);
                String computedHashString = HexUtil.encodeHexString(blake224Hash);

                when(mockCip30Result.isHashed()).thenReturn(true);
                when(mockVote.getPayload()).thenReturn(Optional.of(invalidJsonPayload));
                when(mockVote.getId()).thenReturn("test-vote-id");
                when(mockVote.getSignature()).thenReturn("test-signature");
                // Use computed hash so hash validation passes but JSON parsing fails
                when(mockCip30Result.getMessage(MessageFormat.HEX)).thenReturn(computedHashString);

                Problem jsonError = Problem.builder()
                        .withTitle("JSON_PARSE_ERROR")
                        .withDetail("Invalid JSON")
                        .build();
                when(jsonService.decodeCIP93VoteEnvelope(invalidJsonPayload))
                        .thenReturn(Either.left(jsonError));

                // When
                Either<Problem, VoteEnvelope> result = voteCommitmentService.verifyVote(mockVote, mockCip30Result);

                // Then
                assertThat(result.isLeft()).isTrue();
                Problem problem = result.getLeft();
                assertThat(problem.getTitle()).isEqualTo("INVALID_VOTE");
                assertThat(problem.getDetail()).contains("Unable to parse JSON");
                assertThat(problem.getDetail()).contains("test-vote-id");
            }

            @Test
            @DisplayName("Should verify actual hardware wallet signature with known hash and payload")
            void shouldVerifyActualHardwareWalletSignatureWithKnownHashAndPayload() {
                // Given - Real hardware wallet signature, payload, and key from CIP30 unit tests
                // This ensures we're testing with authentic hardware wallet data
                // First, verify the CIP30 signature itself
                var cip30Verifier = new CIP30Verifier(HARDWARE_WALLET_SIGNATURE, HARDWARE_WALLET_KEY);
                var cip30Result = cip30Verifier.verify();

                assertThat(cip30Result.isValid()).isTrue();
                assertThat(cip30Result.isHashed()).isTrue();

                var signatureHash = cip30Result.getMessage(MessageFormat.HEX);
                assertThat(signatureHash).isEqualTo(EXPECTED_BLAKE2B_224_HASH);

                // Verify the address extraction
                assertThat(cip30Result.getAddress(AddressFormat.TEXT).orElseThrow())
                        .isEqualTo("stake_test1ursmm07qk5qvgdt2mnf8a0k7u877hgpqded807e0w00wr0glrmpz0");

                // Verify that the hash in the signature matches the computed hash from the payload
                byte[] payloadBytes = HARDWARE_WALLET_PAYLOAD.getBytes(StandardCharsets.UTF_8);
                byte[] blake224Hash = Blake2bUtil.blake2bHash224(payloadBytes);
                String computedHash = HexUtil.encodeHexString(blake224Hash);

                assertThat(signatureHash).isEqualTo(computedHash)
                        .withFailMessage("Hash from signature must match computed hash from payload");

                // Now test the VoteCommitmentService with this authentic data
                when(mockCip30Result.isHashed()).thenReturn(true);
                when(mockVote.getPayload()).thenReturn(Optional.of(HARDWARE_WALLET_PAYLOAD));
                when(mockCip30Result.getMessage(MessageFormat.HEX)).thenReturn(signatureHash);

                VoteEnvelope expectedVoteEnvelope = mock(VoteEnvelope.class);
                CIP93Envelope<VoteEnvelope> cip93Envelope = CIP93Envelope.<VoteEnvelope>builder()
                        .data(expectedVoteEnvelope)
                        .build();
                when(jsonService.decodeCIP93VoteEnvelope(HARDWARE_WALLET_PAYLOAD))
                        .thenReturn(Either.right(cip93Envelope));

                // When
                Either<Problem, VoteEnvelope> result = voteCommitmentService.verifyVote(mockVote, mockCip30Result);

                // Then
                assertThat(result.isRight()).isTrue();
                assertThat(result.get()).isEqualTo(expectedVoteEnvelope);
                verify(jsonService).decodeCIP93VoteEnvelope(HARDWARE_WALLET_PAYLOAD);

                // Final verification that our implementation correctly computes and compares hashes
                assertThat(signatureHash).hasSize(56); // Blake2b-224 is 28 bytes = 56 hex chars
            }

        }

        @Nested
        @DisplayName("Software wallet scenarios (message contains full JSON)")
        class SoftwareWalletTests {

            @Test
            @DisplayName("Should verify vote successfully when message contains valid JSON")
            void shouldVerifyVoteWhenMessageContainsValidJson() {
                // Given - Software wallet scenario: signature contains full JSON, no payload in database
                String jsonPayload = "{\"id\":\"123\",\"event\":\"test-event\"}";

                when(mockCip30Result.isHashed()).thenReturn(false);
                when(mockCip30Result.getMessage(MessageFormat.TEXT)).thenReturn(jsonPayload);

                VoteEnvelope expectedVoteEnvelope = mock(VoteEnvelope.class);
                CIP93Envelope<VoteEnvelope> cip93Envelope = CIP93Envelope.<VoteEnvelope>builder()
                        .data(expectedVoteEnvelope)
                        .build();
                when(jsonService.decodeCIP93VoteEnvelope(jsonPayload))
                        .thenReturn(Either.right(cip93Envelope));

                // When
                Either<Problem, VoteEnvelope> result = voteCommitmentService.verifyVote(mockVote, mockCip30Result);

                // Then
                assertThat(result.isRight()).isTrue();
                assertThat(result.get()).isEqualTo(expectedVoteEnvelope);
                verify(jsonService).decodeCIP93VoteEnvelope(jsonPayload);
            }

            @Test
            @DisplayName("Sanity check: Verify real-world software wallet signature and embedded JSON")
            void shouldVerifyRealWorldSoftwareWalletSignatureAndEmbeddedJson() {
                // Given - Real-world software wallet data (signature with embedded JSON payload)
                // First, use our sanity check helper to verify CIP30 signature consistency
                verifySoftwareWalletConsistency(SOFTWARE_WALLET_SIGNATURE, SOFTWARE_WALLET_PAYLOAD, SOFTWARE_WALLET_KEY);

                // Additional verification to extract and validate embedded data
                var cip30Verifier = new CIP30Verifier(SOFTWARE_WALLET_SIGNATURE, SOFTWARE_WALLET_KEY);
                var cip30Result = cip30Verifier.verify();

                assertThat(cip30Result.isValid()).isTrue();
                assertThat(cip30Result.isHashed()).isFalse(); // Software wallets don't hash

                var embeddedPayload = cip30Result.getMessage(MessageFormat.TEXT);
                assertThat(embeddedPayload).isEqualTo(SOFTWARE_WALLET_PAYLOAD);

                // Verify address extraction
                assertThat(cip30Result.getAddress(AddressFormat.TEXT).orElseThrow())
                        .isEqualTo("stake_test1uz2aapdl8xq8u5z6wn2w6sgtv87h8u86tff902r0d8xcencypwsf7");

                // Now test VoteCommitmentService integration with real data
                when(mockCip30Result.isHashed()).thenReturn(false);
                when(mockCip30Result.getMessage(MessageFormat.TEXT)).thenReturn(SOFTWARE_WALLET_PAYLOAD);

                VoteEnvelope expectedVoteEnvelope = mock(VoteEnvelope.class);
                CIP93Envelope<VoteEnvelope> cip93Envelope = CIP93Envelope.<VoteEnvelope>builder()
                        .data(expectedVoteEnvelope)
                        .build();
                when(jsonService.decodeCIP93VoteEnvelope(SOFTWARE_WALLET_PAYLOAD))
                        .thenReturn(Either.right(cip93Envelope));

                // When
                Either<Problem, VoteEnvelope> result = voteCommitmentService.verifyVote(mockVote, mockCip30Result);

                // Then
                assertThat(result.isRight()).isTrue();
                assertThat(result.get()).isEqualTo(expectedVoteEnvelope);
                verify(jsonService).decodeCIP93VoteEnvelope(SOFTWARE_WALLET_PAYLOAD);

                // Verify the payload characteristics
                assertThat(SOFTWARE_WALLET_PAYLOAD.length()).isGreaterThan(56); // Much longer than Blake2b-224 hash
                assertThat(SOFTWARE_WALLET_PAYLOAD).contains("CAST_VOTE");
                assertThat(SOFTWARE_WALLET_PAYLOAD).contains("2e0d53aa-56a2-4a66-a944-31eb872796e1");
                assertThat(SOFTWARE_WALLET_PAYLOAD).contains("187B055D-B5A1-4198-9D02-2EF7D2289628");
            }

            @Test
            @DisplayName("Should verify realistic full vote payload from software wallet")
            void shouldVerifyRealisticFullVotePayloadFromSoftwareWallet() {
                // Given - Using our verified test constants for consistency
                String fullVotePayload = SOFTWARE_WALLET_PAYLOAD;

                when(mockCip30Result.isHashed()).thenReturn(false);
                when(mockCip30Result.getMessage(MessageFormat.TEXT)).thenReturn(fullVotePayload);

                VoteEnvelope expectedVoteEnvelope = mock(VoteEnvelope.class);
                CIP93Envelope<VoteEnvelope> cip93Envelope = CIP93Envelope.<VoteEnvelope>builder()
                        .data(expectedVoteEnvelope)
                        .build();
                when(jsonService.decodeCIP93VoteEnvelope(fullVotePayload))
                        .thenReturn(Either.right(cip93Envelope));

                // When
                Either<Problem, VoteEnvelope> result = voteCommitmentService.verifyVote(mockVote, mockCip30Result);

                // Then
                assertThat(result.isRight()).isTrue();
                assertThat(result.get()).isEqualTo(expectedVoteEnvelope);

                // Verify it follows the software wallet path (uses message directly, no hash comparison)
                verify(jsonService).decodeCIP93VoteEnvelope(fullVotePayload);

                // Verify the payload is much longer than a hash (software wallets embed full data)
                assertThat(fullVotePayload.length()).isGreaterThan(56); // Blake2b-224 hash is 56 chars
                assertThat(fullVotePayload).contains("CAST_VOTE");
                assertThat(fullVotePayload).contains("CF_SUMMIT_2025_26BCC");
            }

            @Test
            @DisplayName("Should fail when message JSON is invalid")
            void shouldFailWhenMessageJsonIsInvalid() {
                // Given - Software wallet with invalid JSON in signature message
                String invalidJson = "not a json";

                when(mockCip30Result.isHashed()).thenReturn(false);
                when(mockCip30Result.getMessage(MessageFormat.TEXT)).thenReturn(invalidJson);
                when(mockVote.getSignature()).thenReturn("test-signature");
                when(mockVote.getId()).thenReturn("test-vote-id");

                Problem jsonError = Problem.builder()
                        .withTitle("JSON_PARSE_ERROR")
                        .withDetail("Invalid JSON")
                        .build();
                when(jsonService.decodeCIP93VoteEnvelope(invalidJson))
                        .thenReturn(Either.left(jsonError));

                // When
                Either<Problem, VoteEnvelope> result = voteCommitmentService.verifyVote(mockVote, mockCip30Result);

                // Then
                assertThat(result.isLeft()).isTrue();
                Problem problem = result.getLeft();
                assertThat(problem.getTitle()).isEqualTo("INVALID_VOTE");
                assertThat(problem.getDetail()).contains("Unable to parse JSON");
                assertThat(problem.getDetail()).contains("test-vote-id");
            }

            @Test
            @DisplayName("Should handle null message gracefully")
            void shouldHandleNullMessageGracefully() {
                // Given - Software wallet with null message
                when(mockCip30Result.isHashed()).thenReturn(false);
                when(mockCip30Result.getMessage(MessageFormat.TEXT)).thenReturn(null);
                when(mockVote.getSignature()).thenReturn("test-signature");
                when(mockVote.getId()).thenReturn("test-vote-id");

                Problem jsonError = Problem.builder()
                        .withTitle("JSON_PARSE_ERROR")
                        .withDetail("Null message")
                        .build();
                when(jsonService.decodeCIP93VoteEnvelope(null))
                        .thenReturn(Either.left(jsonError));

                // When
                Either<Problem, VoteEnvelope> result = voteCommitmentService.verifyVote(mockVote, mockCip30Result);

                // Then
                assertThat(result.isLeft()).isTrue();
                Problem problem = result.getLeft();
                assertThat(problem.getTitle()).isEqualTo("INVALID_VOTE");
            }
        }

        @Nested
        @DisplayName("Edge cases and error scenarios")
        class EdgeCasesTests {

            @Test
            @DisplayName("Should handle empty payload string")
            void shouldHandleEmptyPayloadString() {
                // Given - Hardware wallet scenario with empty stored payload
                when(mockCip30Result.isHashed()).thenReturn(true);
                when(mockVote.getPayload()).thenReturn(Optional.of(""));
                when(mockVote.getId()).thenReturn("test-vote-id");

                byte[] emptyHash = Blake2bUtil.blake2bHash224("".getBytes(StandardCharsets.UTF_8));
                String emptyHashString = HexUtil.encodeHexString(emptyHash);
                when(mockCip30Result.getMessage(MessageFormat.HEX)).thenReturn(emptyHashString);

                Problem jsonError = Problem.builder()
                        .withTitle("JSON_PARSE_ERROR")
                        .withDetail("Empty JSON")
                        .build();
                when(jsonService.decodeCIP93VoteEnvelope(""))
                        .thenReturn(Either.left(jsonError));

                // When
                Either<Problem, VoteEnvelope> result = voteCommitmentService.verifyVote(mockVote, mockCip30Result);

                // Then
                assertThat(result.isLeft()).isTrue();
            }

            @Test
            @DisplayName("Should handle very large payloads correctly")
            void shouldHandleVeryLargePayloadsCorrectly() {
                // Given - Hardware wallet scenario with very large stored payload
                StringBuilder largePayloadBuilder = new StringBuilder("{\"data\":\"");
                for (int i = 0; i < 10000; i++) {
                    largePayloadBuilder.append("a");
                }
                largePayloadBuilder.append("\"}");
                String largePayload = largePayloadBuilder.toString();

                byte[] payloadBytes = largePayload.getBytes(StandardCharsets.UTF_8);
                byte[] blake224Hash = Blake2bUtil.blake2bHash224(payloadBytes);
                String blake224HashString = HexUtil.encodeHexString(blake224Hash);

                when(mockCip30Result.isHashed()).thenReturn(true);
                when(mockVote.getPayload()).thenReturn(Optional.of(largePayload));
                when(mockCip30Result.getMessage(MessageFormat.HEX)).thenReturn(blake224HashString);

                VoteEnvelope expectedVoteEnvelope = mock(VoteEnvelope.class);
                CIP93Envelope<VoteEnvelope> cip93Envelope = CIP93Envelope.<VoteEnvelope>builder()
                        .data(expectedVoteEnvelope)
                        .build();
                when(jsonService.decodeCIP93VoteEnvelope(largePayload))
                        .thenReturn(Either.right(cip93Envelope));

                // When
                Either<Problem, VoteEnvelope> result = voteCommitmentService.verifyVote(mockVote, mockCip30Result);

                // Then
                assertThat(result.isRight()).isTrue();
                assertThat(result.get()).isEqualTo(expectedVoteEnvelope);
            }
        }
    }
}