package br.com.vs.currency.converter.resource.dto.request;

import br.com.vs.currency.converter.model.enums.Currency;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

import static br.com.vs.currency.converter.utils.Messages.CONVERSION_AMOUNT_POSITIVE_MESSAGE;
import static br.com.vs.currency.converter.utils.Messages.CURRENCY_NOT_NULL_MESSAGE;
import static br.com.vs.currency.converter.utils.Messages.USER_ID_POSITIVE_MESSAGE;

@NoArgsConstructor
@AllArgsConstructor
@Getter
public class ConversionRequest {

    @Positive(message = USER_ID_POSITIVE_MESSAGE)
    private Long userId;

    @NotNull(message = CURRENCY_NOT_NULL_MESSAGE)
    private Currency sourceCurrency;

    @Positive(message = CONVERSION_AMOUNT_POSITIVE_MESSAGE)
    private BigDecimal amount;

    @NotNull(message = CURRENCY_NOT_NULL_MESSAGE)
    private Currency targetCurrency;
}
