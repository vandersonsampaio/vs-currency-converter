package br.com.vs.currency.converter.model.entity;

import br.com.vs.currency.converter.model.exception.ServerErrorException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.math.BigDecimal;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

class ConversionTest {

    @Test
    @DisplayName("Should generate Id for Conversion")
    void generateId() {
        var actual = new Conversion();
        assertNull(actual.getId());
        actual.generateId();
        assertNotNull(actual.getId());
    }

    @Test
    @DisplayName("Should calculate conversion")
    void calculateTarget() {
        BigDecimal rateSource = BigDecimal.ONE;
        BigDecimal rateTarget = BigDecimal.TWO;
        var actual = Conversion.builder().sourceAmount(BigDecimal.TEN).build();

        actual.calculateTarget(rateSource, rateTarget);

        assertEquals(rateSource, actual.getRateSource());
        assertEquals(rateTarget, actual.getRateTarget());
        assertEquals(2D, actual.getRateCompose().doubleValue());
        assertEquals(20D, actual.getTargetAmount().doubleValue());
    }

    static Stream<Arguments> provideRates() {
        return Stream.of(
                Arguments.of(null, null),
                Arguments.of(BigDecimal.ONE, null),
                Arguments.of(null, BigDecimal.ONE),
                Arguments.of(BigDecimal.ZERO, BigDecimal.ZERO),
                Arguments.of(BigDecimal.ZERO, BigDecimal.TEN),
                Arguments.of(BigDecimal.TEN, BigDecimal.ZERO)
        );
    }

    @ParameterizedTest
    @MethodSource("provideRates")
    @DisplayName("Should throw exception to calculate conversion")
    void throwExceptionCalculateTarget(BigDecimal rateSource, BigDecimal amount) {
        var actual = Conversion.builder().sourceAmount(amount).build();

        assertThrows(ServerErrorException.class, () -> actual.calculateTarget(rateSource, BigDecimal.TEN));
    }
}
