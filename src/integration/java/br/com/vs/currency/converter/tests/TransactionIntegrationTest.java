package br.com.vs.currency.converter.tests;

import br.com.vs.currency.converter.helper.TransactionHelper;
import br.com.vs.currency.converter.model.enums.Currency;
import br.com.vs.currency.converter.resource.dto.response.ConversionResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

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

        ConversionResponse conversion = getConversionById(id);

        assertThat(conversion).isNotNull();
        assertThat(conversion.getUserId()).isEqualTo(userId);
        assertThat(conversion.getSourceCurrency()).isEqualTo(sourceCurrency);
        assertThat(conversion.getTargetCurrency()).isEqualTo(targetCurrency);
        assertThat(conversion.getAmount()).isEqualTo(amount);
        assertThat(conversion.getRate()).isPositive();
        assertThat(conversion.getTargetAmount()).isPositive();
        assertThat(conversion.getTimestamp()).isNotNull();
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
        var error = getConversionByIdBadRequest(id);

        assertThat(error).isNotNull();
        assertThat(error.getMessage()).contains("tamanho deve ser entre");
    }

    @Test
    @DisplayName("Should List All Conversion Transaction Object by User ID")
    void findAllConversionTransactionsByUser() {
        Long userId = 11L;
        Currency sourceCurrency = Currency.BRL;
        Currency targetCurrency = Currency.USD;
        BigDecimal amount = BigDecimal.valueOf(196.03);

        createConversion(userId, sourceCurrency, targetCurrency, amount);

        List<ConversionResponse> conversion = findAllByUserId(userId);

        assertThat(conversion).isNotNull();
        assertThat(conversion).hasSize(1);
        assertThat(conversion.get(0).getUserId()).isEqualTo(userId);
        assertThat(conversion.get(0).getSourceCurrency()).isEqualTo(sourceCurrency);
        assertThat(conversion.get(0).getTargetCurrency()).isEqualTo(targetCurrency);
        assertThat(conversion.get(0).getAmount()).isEqualTo(amount);
        assertThat(conversion.get(0).getRate()).isPositive();
        assertThat(conversion.get(0).getTargetAmount()).isPositive();
        assertThat(conversion.get(0).getTimestamp()).isNotNull();
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
        var error = findAllByUserIdBadRequest((long) userId);

        assertThat(error).isNotNull();
        assertThat(error.getMessage()).contains("deve ser maior");
    }

    //criar conversão
    //conversão com valor zerado
    //conversão com mesma origem e destino
    //conversão de valores negativos
    //conversão de moeda que não existe
    //userId inválido


    //cenário onde a API está fora
    //cenário onde a API estourou o numero maximo de requisições
    //cenário onde a API deu não autorizado
    //cenário onde a API não encontrou o recurso
    //cenário de erro interno não esperado
}
