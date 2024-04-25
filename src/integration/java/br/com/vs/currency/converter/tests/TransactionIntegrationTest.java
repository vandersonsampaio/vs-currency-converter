package br.com.vs.currency.converter.tests;

import br.com.vs.currency.converter.helper.TransactionHelper;
import br.com.vs.currency.converter.model.enums.Currency;
import br.com.vs.currency.converter.resource.dto.response.ConversionResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.http.HttpStatus;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

public class TransactionIntegrationTest extends TransactionHelper {

    @Override
    @BeforeEach
    public void init() {
        super.init();
    }

    @Test
    @DisplayName("Should Return the Conversion Transaction Object by ID")
    void getConversionTransaction() {
        Long userId = 1L;
        Currency sourceCurrency = Currency.BRL;
        Currency targetCurrency = Currency.USD;
        BigDecimal amount = BigDecimal.valueOf(135.52);

        String id = createConversion(userId, sourceCurrency, targetCurrency, amount);

        ConversionResponse actual = getConversionById(id);

        assertResult(actual, userId, sourceCurrency, targetCurrency, amount);
    }

    @Test
    @DisplayName("Should Return an exception when Id not exists")
    void throwExceptionWhenGetConversionNotFound() {
        String id = UUID.randomUUID().toString();

        var error = getConversionByIdNotFound(id);

        assertThat(error).isNotNull();
        assertThat(error.getMessage()).contains("Conversion Transaction Not Found");
    }

    @ParameterizedTest
    @ValueSource(strings = { "550e8400-e29b-41d4-a716-446655440000-1", "550e8400-e29b-41d4-a716-446"})
    @DisplayName("Should Return an exception when Id is invalid")
    void throwExceptionWhenGetConversionByInvalidId(String id) {
        String message = "Id must be exactly 36 characters";
        var error = getConversionByIdBadRequest(id);

        assertThat(error).isNotNull();
        assertThat(error.getMessage()).isEqualTo(message);
    }

    @Test
    @DisplayName("Should List All Conversion Transaction Object by User ID")
    void findAllConversionTransactionsByUser() {
        Long userId = 11L;
        Currency sourceCurrency = Currency.BRL;
        Currency targetCurrency = Currency.USD;
        BigDecimal amount = BigDecimal.valueOf(196.03);

        createConversion(userId, sourceCurrency, targetCurrency, amount);

        List<ConversionResponse> actual = findAllByUserId(userId);

        assertThat(actual).isNotNull();
        assertThat(actual).hasSize(1);
        assertResult(actual.get(0), userId, sourceCurrency, targetCurrency, amount);
    }

    @Test
    @DisplayName("Should Empty List when User Id not exists")
    void findEmptyListWhenUserIdNotExists() {
        Long userId = 12L;

        List<ConversionResponse> conversion = findAllByUserId(userId);

        assertThat(conversion).isNotNull();
        assertThat(conversion).hasSize(0);
    }

    @ParameterizedTest
    @ValueSource(ints = { -1, 0})
    @DisplayName("Should Return an exception when User Id is invalid")
    void throwExceptionWhenFindConversionByInvalidId(int userId) {
        String message = "User Id must be greater than zero";
        var error = findAllByUserIdBadRequest((long) userId);

        assertThat(error).isNotNull();
        assertThat(error.getMessage()).isEqualTo(message);
    }

    @Test
    @DisplayName("Should create a conversion transaction")
    void createConversion() {
        Long userId = 21L;
        Currency source = Currency.JPY;
        Currency target = Currency.USD;
        BigDecimal amount = BigDecimal.valueOf(3.57);

        externalApiSuccess();

        var actual = converter(userId, source, target, amount);

        assertResult(actual, userId, source, target, amount);
    }

    @Test
    @DisplayName("Should create a conversion transaction when source and target currencies are the same")
    void createConversionWhenSourceAndTargetAreSame() {
        Long userId = 22L;
        Currency source = Currency.BRL;
        Currency target = Currency.BRL;
        BigDecimal amount = BigDecimal.valueOf(3.57);

        var actual = converter(userId, source, target, amount);

        assertResult(actual, userId, source, target, amount);
        assertThat(actual.getTargetAmount()).isEqualTo(actual.getAmount());
    }

    @Test
    @DisplayName("Should throw exception to create a conversion transaction when amount is zero")
    void createConversionWhenAmountIsZero() {
        String message = "Amount must be greater than zero";
        Long userId = 23L;
        Currency source = Currency.BRL;
        Currency target = Currency.EUR;
        BigDecimal amount = BigDecimal.ZERO;

        var actual = converterBadRequest(userId, source, target, amount);

        assertThat(actual.getStatus()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(actual.getMessage()).contains(message);
    }

    @Test
    @DisplayName("Should throw exception to create a conversion transaction when amount is negative")
    void createConversionWhenAmountIsNegative() {
        String message = "Amount must be greater than zero";
        Long userId = 24L;
        Currency source = Currency.BRL;
        Currency target = Currency.JPY;
        BigDecimal amount = BigDecimal.valueOf(-1);

        var actual = converterBadRequest(userId, source, target, amount);

        assertThat(actual.getStatus()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(actual.getMessage()).contains(message);
    }

    @Test
    @DisplayName("Should throw exception to create a conversion transaction when user id is invalid")
    void createConversionWhenUserIsInvalid() {
        String message = "User Id must be greater than zero";
        Long userId = -1L;
        Currency source = Currency.USD;
        Currency target = Currency.BRL;
        BigDecimal amount = BigDecimal.valueOf(1);

        var actual = converterBadRequest(userId, source, target, amount);

        assertThat(actual.getStatus()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(actual.getMessage()).contains(message);
    }

    @Test
    @DisplayName("Should throw exception to create a conversion transaction when currency is null")
    void createConversionWhenCurrencyIsNull() {
        String message = "Currency must be not null value";
        Long userId = 25L;
        Currency target = Currency.BRL;
        BigDecimal amount = BigDecimal.valueOf(11);

        var actual = converterBadRequest(userId, null, target, amount);

        assertThat(actual.getStatus()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(actual.getMessage()).contains(message);
    }

    @Test
    @DisplayName("Should throw exception to create a conversion transaction because external API not found")
    void createConversionWhenExternalAPINotFound() {
        String message = "is not found";
        Long userId = 26L;
        Currency source = Currency.JPY;
        Currency target = Currency.BRL;
        BigDecimal amount = BigDecimal.valueOf(11);

        externalApiNotFound();

        var actual = converterFailedDependency(userId, source, target, amount);

        assertThat(actual.getMessage()).contains(message);
    }

    @Test
    @DisplayName("Should throw exception to create a conversion transaction because external API has too many requests")
    void createConversionWhenExternalAPIManyRequest() {
        String message = "You have exceeded your daily";
        Long userId = 27L;
        Currency source = Currency.JPY;
        Currency target = Currency.BRL;
        BigDecimal amount = BigDecimal.valueOf(11);

        externalApiManyRequest();

        var actual = converterFailedDependency(userId, source, target, amount);

        assertThat(actual.getMessage()).contains(message);
    }

    @Test
    @DisplayName("Should throw exception to create a conversion transaction because external API Unauthorized")
    void createConversionWhenExternalAPIUnauthorized() {
        String message = "It is necessary to informe";
        Long userId = 28L;
        Currency source = Currency.JPY;
        Currency target = Currency.BRL;
        BigDecimal amount = BigDecimal.valueOf(11);

        externalApiUnauthorized();

        var actual = converterFailedDependency(userId, source, target, amount);

        assertThat(actual.getMessage()).contains(message);
    }

    @Test
    @DisplayName("Should throw exception to create a conversion transaction because external API Bad Request")
    void createConversionWhenExternalAPIBadRequest() {
        String message = "Bad Request";
        Long userId = 29L;
        Currency source = Currency.JPY;
        Currency target = Currency.BRL;
        BigDecimal amount = BigDecimal.valueOf(11);

        externalApiBadRequest();

        var actual = converterFailedDependency(userId, source, target, amount);

        assertThat(actual.getMessage()).contains(message);
    }

    @Test
    @DisplayName("Should throw exception to create a conversion transaction because external API Internal Error")
    void createConversionWhenExternalAPIInternalError() {
        String message = "Internal Error";
        Long userId = 30L;
        Currency source = Currency.JPY;
        Currency target = Currency.BRL;
        BigDecimal amount = BigDecimal.valueOf(11);

        externalApiInternalError();

        var actual = converterFailedDependency(userId, source, target, amount);

        assertThat(actual.getMessage()).contains(message);
    }

    private void assertResult(ConversionResponse actual, Long userId, Currency source,
                              Currency target, BigDecimal amount) {
        assertThat(actual).isNotNull();
        assertThat(actual.getUserId()).isEqualTo(userId);
        assertThat(actual.getSourceCurrency()).isEqualTo(source);
        assertThat(actual.getTargetCurrency()).isEqualTo(target);
        assertThat(actual.getAmount()).isEqualTo(amount);
        assertThat(actual.getRate()).isPositive();
        assertThat(actual.getTargetAmount()).isPositive();
        assertThat(actual.getTimestamp()).isNotNull();
    }
}
