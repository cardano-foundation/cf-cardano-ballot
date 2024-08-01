package org.cardano.foundation.voting.utils;

import io.vavr.control.Either;
import org.junit.jupiter.api.Test;
import org.zalando.problem.Problem;

import static org.assertj.core.api.Assertions.assertThat;

class KeriTest {

    @Test
    void testAidIsNull() {
        Either<Problem, Boolean> result = Keri.checkAid(null);
        assertThat(result.isLeft()).isTrue();
        assertThat(result.getLeft().getTitle()).isEqualTo("INVALID_KERI_AID");
        assertThat(result.getLeft().getDetail()).isEqualTo("Aid is required");
    }

    @Test
    void testAidIsEmpty() {
        Either<Problem, Boolean> result = Keri.checkAid("");
        assertThat(result.isLeft()).isTrue();
        assertThat(result.getLeft().getTitle()).isEqualTo("INVALID_KERI_AID");
        assertThat(result.getLeft().getDetail()).isEqualTo("Aid is required");
    }

    @Test
    void testAidInvalidLength() {
        Either<Problem, Boolean> result = Keri.checkAid("EIA1PcKQkcW6mvs2kVwVpvaf6SMuBHLMCrx57WPW6UPOX");
        assertThat(result.isLeft()).isTrue();
        assertThat(result.getLeft().getTitle()).isEqualTo("INVALID_KERI_AID");
        assertThat(result.getLeft().getDetail()).isEqualTo("Aid must be 44 characters long");
    }

    @Test
    void testAidInvalidFirstChar() {
        Either<Problem, Boolean> result = Keri.checkAid("AIA1PcKQkcW6mvs2kVwVpvaf6SMuBHLMCrx57WPW6UPO");
        assertThat(result.isLeft()).isTrue();
        assertThat(result.getLeft().getTitle()).isEqualTo("INVALID_KERI_AID");
        assertThat(result.getLeft().getDetail()).isEqualTo("Aid must start with 'E' or 'B'");
    }

    @Test
    void testValidAidStartsWithE() {
        Either<Problem, Boolean> result = Keri.checkAid("EIA1PcKQkcW6mvs2kVwVpvaf6SMuBHLMCrx57WPW6UPO");
        assertThat(result.isRight()).isTrue();
        assertThat(result.get()).isTrue();
    }

    @Test
    void testValidAidStartsWithB() {
        Either<Problem, Boolean> result = Keri.checkAid("BbIg_3-11d3PYxSInLN-Q9_T2axD6kkXd3XRgbGZTm6s");
        assertThat(result.isRight()).isTrue();
        assertThat(result.get()).isTrue();
    }

}