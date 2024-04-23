package br.com.vs.currency.converter.client.dto;

import br.com.vs.currency.converter.model.enums.Currency;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Map;

@AllArgsConstructor
@NoArgsConstructor
@Getter
public class ExchangeRateResponse {

    private Currency base;
    private LocalDate date;
    private Map<Currency, BigDecimal> rates;
    private boolean success;
    private long timestamp;
}
