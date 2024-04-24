package br.com.vs.currency.converter.resource.dto.request;

import br.com.vs.currency.converter.model.enums.Currency;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@NoArgsConstructor
@AllArgsConstructor
@Getter
public class ConversionRequest {

    @Positive
    private Long userId;
    @NotNull
    private Currency sourceCurrency;
    @Positive
    private BigDecimal amount;
    @NotNull
    private Currency targetCurrency;
}
