package br.com.vs.currency.converter.resource;

import br.com.vs.currency.converter.model.entity.Conversion;
import br.com.vs.currency.converter.model.enums.Currency;
import br.com.vs.currency.converter.resource.dto.mapper.ConversionMapperImpl;
import br.com.vs.currency.converter.resource.dto.request.ConversionRequest;
import br.com.vs.currency.converter.resource.dto.response.ConversionResponse;
import br.com.vs.currency.converter.service.TransactionService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.test.util.ReflectionTestUtils;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ConversionControllerTest {

    private static final String ID = UUID.randomUUID().toString();
    private static final Long USER_ID = 1L;
    private static final Currency SOURCE_CURRENCY = Currency.BRL;
    private static final Currency TARGET_CURRENCY = Currency.USD;
    private static final BigDecimal AMOUNT = BigDecimal.TEN;
    private static final BigDecimal RATE = BigDecimal.ONE;
    private static final BigDecimal TARGET_AMOUNT = BigDecimal.TWO;
    @Mock
    private TransactionService service;

    @InjectMocks
    private ConversionController controller;

    @Captor
    private ArgumentCaptor<Conversion> conversionCaptor;

    @BeforeEach
    public void init() {
        ReflectionTestUtils.setField(controller, "mapper", new ConversionMapperImpl());
    }

    @Test
    @DisplayName("Should Return one Conversion Transaction")
    void getTransaction() {
        when(service.getTransaction(ID)).thenReturn(buildConversion());

        var actual = controller.getTransaction(ID);

        assertEquals(HttpStatus.OK, actual.getStatusCode());
        assertResult(actual.getBody());

        verify(service).getTransaction(ID);
        verifyNoMoreInteractions(service);
    }

    @Test
    @DisplayName("Should List All Conversion Transactions by User Id")
    void findAllByUser() {

        when(service.findTransactions(USER_ID)).thenReturn(List.of(buildConversion()));

        var actual = controller.findAllByUser(USER_ID);

        assertEquals(HttpStatus.OK, actual.getStatusCode());
        assertNotNull(actual.getBody());
        assertFalse(actual.getBody().isEmpty());
        assertResult(actual.getBody().get(0));

        verify(service).findTransactions(USER_ID);
        verifyNoMoreInteractions(service);
    }

    @Test
    @DisplayName("Should Create a Conversion Transactions")
    void createConversion() {
        ConversionRequest request = new ConversionRequest(USER_ID, SOURCE_CURRENCY, AMOUNT, TARGET_CURRENCY);

        when(service.converter(any())).thenReturn(buildConversion());

        var actual = controller.createConversion(request);

        assertEquals(HttpStatus.CREATED, actual.getStatusCode());
        assertNotNull(actual.getBody());
        assertResult(actual.getBody());

        verify(service).converter(conversionCaptor.capture());
        verifyNoMoreInteractions(service);

        assertNull(conversionCaptor.getValue().getId());
        assertNull(conversionCaptor.getValue().getRateSource());
        assertNull(conversionCaptor.getValue().getRateTarget());
        assertNull(conversionCaptor.getValue().getRateCompose());
        assertNull(conversionCaptor.getValue().getRegisterTime());
        assertNull(conversionCaptor.getValue().getTargetAmount());
        assertEquals(USER_ID, conversionCaptor.getValue().getUserId());
        assertEquals(SOURCE_CURRENCY, conversionCaptor.getValue().getSourceCurrency());
        assertEquals(TARGET_CURRENCY, conversionCaptor.getValue().getTargetCurrency());
        assertEquals(AMOUNT, conversionCaptor.getValue().getSourceAmount());
    }

    private void assertResult(ConversionResponse actual) {
        assertNotNull(actual);
        assertEquals(ID, actual.getId());
        assertEquals(USER_ID, actual.getUserId());
        assertEquals(SOURCE_CURRENCY, actual.getSourceCurrency());
        assertEquals(TARGET_CURRENCY, actual.getTargetCurrency());
        assertEquals(AMOUNT, actual.getAmount());
        assertEquals(TARGET_AMOUNT, actual.getTargetAmount());
        assertEquals(RATE, actual.getRate());
        assertNotNull(actual.getTimestamp());
    }

    private Conversion buildConversion() {
        return new Conversion(ID, USER_ID, SOURCE_CURRENCY, AMOUNT, TARGET_CURRENCY, TARGET_AMOUNT, BigDecimal.ONE,
                BigDecimal.ONE, RATE, LocalDateTime.now());
    }
}