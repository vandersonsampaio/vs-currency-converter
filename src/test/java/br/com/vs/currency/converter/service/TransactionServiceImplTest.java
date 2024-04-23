package br.com.vs.currency.converter.service;

import br.com.vs.currency.converter.model.entity.Conversion;
import br.com.vs.currency.converter.model.enums.Currency;
import br.com.vs.currency.converter.model.exception.NotFoundException;
import br.com.vs.currency.converter.model.exception.ServerErrorException;
import br.com.vs.currency.converter.model.repository.ConversionRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TransactionServiceImplTest {

    private static final BigDecimal BRL_VALUE = BigDecimal.valueOf(5.49);
    private static final BigDecimal JPY_VALUE = BigDecimal.valueOf(165.70);
    @Mock
    private ExchangeService exchangeService;
    @Mock
    private ConversionRepository repository;

    @InjectMocks
    private TransactionServiceImpl service;

    @Captor
    private ArgumentCaptor<Conversion> conversionCaptor;

    static Stream<Arguments> provideCurrencies() {
        return Stream.of(
                Arguments.of(Currency.BLR, Currency.BLR),
                Arguments.of(Currency.JPY, Currency.JPY),
                Arguments.of(Currency.USD, Currency.USD),
                Arguments.of(Currency.EUR, Currency.EUR)
        );
    }

    @ParameterizedTest
    @MethodSource("provideCurrencies")
    @DisplayName("Should return the conversion transaction for same Currency")
    void converterSameCurrency(Currency source, Currency target) {
        Conversion conversion = Conversion.builder().sourceAmount(BigDecimal.TEN)
                .sourceCurrency(source).targetCurrency(target).build();

        service.converter(conversion);

        verify(repository).save(conversionCaptor.capture());

        verifyNoMoreInteractions(repository);
        verifyNoInteractions(exchangeService);

        assertConversionValues(conversionCaptor.getValue(), 10D, 1D, 1D, 1D);
    }

    @Test
    @DisplayName("Should throw exception when amount value is zero")
    void throwsExcpetionWhenAmountIsZero() {
        Conversion conversion = Conversion.builder().sourceAmount(BigDecimal.ZERO)
                .sourceCurrency(Currency.EUR).targetCurrency(Currency.BLR).build();

        when(exchangeService.rates()).thenReturn(buildRates());

        assertThrows(ServerErrorException.class, () -> service.converter(conversion));

        verify(exchangeService).rates();
        verifyNoMoreInteractions(exchangeService, repository);
    }

    @Test
    @DisplayName("Should return the conversion transaction when source Currency is base")
    void converterWhenSourceCurrencyIsBase() {
        Conversion conversion = Conversion.builder().sourceAmount(BigDecimal.TEN)
                .sourceCurrency(Currency.EUR).targetCurrency(Currency.BLR).build();

        when(exchangeService.rates()).thenReturn(buildRates());

        service.converter(conversion);

        verify(repository).save(conversionCaptor.capture());
        verify(exchangeService).rates();
        verifyNoMoreInteractions(exchangeService, repository);

        double targetAmount = 54.9D;
        assertConversionValues(conversionCaptor.getValue(), targetAmount, BRL_VALUE.doubleValue(),
                BRL_VALUE.doubleValue(), 1D);
    }

    @Test
    @DisplayName("Should return the conversion transaction when target Currency is base")
    void converterWhenTargetCurrencyIsBase() {
        Conversion conversion = Conversion.builder().sourceAmount(BigDecimal.TEN)
                .sourceCurrency(Currency.BLR).targetCurrency(Currency.EUR).build();

        when(exchangeService.rates()).thenReturn(buildRates());

        service.converter(conversion);

        verify(repository).save(conversionCaptor.capture());
        verify(exchangeService).rates();
        verifyNoMoreInteractions(exchangeService, repository);

        double targetAmount = 1.82D;
        double rateCompose = .182D;
        assertConversionValues(conversionCaptor.getValue(), targetAmount, rateCompose, 1D, BRL_VALUE.doubleValue());
    }

    @Test
    @DisplayName("Should return the conversion transaction when Currencies is not base")
    void converterWhenCurrencyIsNotBase() {
        Conversion conversion = Conversion.builder().sourceAmount(BigDecimal.TEN)
                .sourceCurrency(Currency.BLR).targetCurrency(Currency.JPY).build();

        when(exchangeService.rates()).thenReturn(buildRates());

        service.converter(conversion);

        verify(repository).save(conversionCaptor.capture());
        verify(exchangeService).rates();
        verifyNoMoreInteractions(exchangeService, repository);

        double targetAmount = 301.82D;
        double rateCompose = 30.182D;
        assertConversionValues(conversionCaptor.getValue(), targetAmount, rateCompose, JPY_VALUE.doubleValue(), BRL_VALUE.doubleValue());
    }

    @Test
    @DisplayName("Should return all transactions by UserId")
    void findTransactions() {
        Long userId = 1L;

        service.findTransactions(userId);

        verify(repository).findAllBiUserId(userId);
        verifyNoMoreInteractions(repository);
        verifyNoInteractions(exchangeService);
    }

    @Test
    @DisplayName("Should return one transaction by Id")
    void getTransaction() {
        String id = "uuid";

        when(repository.findById(id)).thenReturn(Optional.of(new Conversion()));

        service.getTransaction(id);

        verify(repository).findById(id);
        verifyNoMoreInteractions(repository);
        verifyNoInteractions(exchangeService);
    }

    @Test
    @DisplayName("Should throws exception when transaction not found")
    void throwsExcpetionWhenTransactionNotFound() {
        String id = "uuid";

        when(repository.findById(id)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> service.getTransaction(id));

        verify(repository).findById(id);
        verifyNoMoreInteractions(repository);
        verifyNoInteractions(exchangeService);
    }

    private void assertConversionValues(Conversion actual, double targetAmount, double rateCompose,
                                        double rateTarget, double rateSource) {
        assertEquals(targetAmount, actual.getTargetAmount().doubleValue());
        assertEquals(rateCompose, actual.getRateCompose().doubleValue());
        assertEquals(rateTarget, actual.getRateTarget().doubleValue());
        assertEquals(rateSource, actual.getRateSource().doubleValue());
        assertFalse(actual.getId().isBlank());
    }

    private Map<Currency, BigDecimal> buildRates() {
        return Map.of(Currency.BLR, BRL_VALUE,
                Currency.JPY, JPY_VALUE,
                Currency.USD, BigDecimal.valueOf(1.07));
    }
}