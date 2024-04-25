package br.com.vs.currency.converter.service;

import br.com.vs.currency.converter.client.ExchangeRateClient;
import br.com.vs.currency.converter.client.dto.ExchangeRateResponse;
import br.com.vs.currency.converter.model.enums.Currency;
import br.com.vs.currency.converter.model.exception.ServerErrorException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;
import org.springframework.test.util.ReflectionTestUtils;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ExchangeServiceImplTest {

    private static final Map<Currency, BigDecimal> RATES = Map.of(Currency.BRL, BigDecimal.TEN,
            Currency.JPY, BigDecimal.ONE);

    @Mock
    private ExchangeRateClient client;

    @InjectMocks
    private ExchangeServiceImpl service;

    @BeforeEach
    public void init() {
        ReflectionTestUtils.setField(service, "apiKey", "0001");
    }

    @Test
    @DisplayName("Should throw exception because there is no base currency")
    void throwExceptionWhenGetRatesNoBaseCurrency() {
        ReflectionTestUtils.setField(Currency.EUR, "base", false);

        assertFalse(Currency.EUR.isBase());
        assertThrows(ServerErrorException.class, () -> service.rates());

        ReflectionTestUtils.setField(Currency.EUR, "base", true);
        assertTrue(Currency.EUR.isBase());

        verifyNoInteractions(client);
    }

    @Test
    @DisplayName("Should throw exception because the client returns not OK")
    void throwExceptionWhenGetRatesClientNotOK() {
        when(client.latest(anyString(), any())).thenReturn(ResponseEntity.notFound().build());

        assertThrows(ServerErrorException.class, () -> service.rates());

        verify(client).latest(anyString(), any());
        verifyNoMoreInteractions(client);
    }

    @Test
    @DisplayName("Should return Rates")
    void shouldReturnRates() {
        when(client.latest(anyString(), any())).thenReturn(ResponseEntity.ok(buildResponse()));

        var actual = service.rates();

        assertSame(RATES, actual);

        verify(client).latest(anyString(), any());
        verifyNoMoreInteractions(client);
    }

    private ExchangeRateResponse buildResponse() {
        return new ExchangeRateResponse(Currency.EUR, LocalDate.now(), RATES, true, System.currentTimeMillis());
    }
}
