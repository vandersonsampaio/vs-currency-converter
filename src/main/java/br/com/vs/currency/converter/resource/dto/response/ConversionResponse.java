package br.com.vs.currency.converter.resource.dto.response;

import br.com.vs.currency.converter.model.enums.Currency;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Builder
public class ConversionResponse {

    private String id;
    private Long userId;
    private Currency sourceCurrency;
    private BigDecimal amount;
    private Currency targetCurrency;
    private BigDecimal targetAmount;
    private BigDecimal rate;
    private LocalDateTime timestamp;
}
