package br.com.vs.currency.converter.service;

import br.com.vs.currency.converter.model.enums.Currency;

import java.math.BigDecimal;
import java.util.Map;

public interface ExchangeService {

    Map<Currency, BigDecimal> rates();
}
