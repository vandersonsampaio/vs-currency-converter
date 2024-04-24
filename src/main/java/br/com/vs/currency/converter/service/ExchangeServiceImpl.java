package br.com.vs.currency.converter.service;

import br.com.vs.currency.converter.client.ExchangeRateClient;
import br.com.vs.currency.converter.model.enums.Currency;
import br.com.vs.currency.converter.model.exception.ServerErrorException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

import static br.com.vs.currency.converter.config.CacheConfig.CACHE_NAME;
import static br.com.vs.currency.converter.utils.Messages.BASE_CURRENCY_ERROR_MESSAGE;
import static br.com.vs.currency.converter.utils.Messages.GET_EXCHANGE_RATES_ERROR_MESSAGE;

@RequiredArgsConstructor
@Service
public class ExchangeServiceImpl implements ExchangeService {

    @Value("${exchange.server.key}")
    private String apiKey;
    private final ExchangeRateClient client;

    @Cacheable(value = CACHE_NAME)
    @Override
    public Map<Currency, BigDecimal> rates() {
        var request = buildRequest();
        var response = client.latest(apiKey, request);

        if (response.getStatusCode().value() != HttpStatus.OK.value()) {
            throw new ServerErrorException(GET_EXCHANGE_RATES_ERROR_MESSAGE);
        }

        return Objects.requireNonNull(response.getBody()).getRates();
    }

    private Map<String, String> buildRequest() {
        Currency currencyBase = Arrays.stream(Currency.values()).filter(Currency::isBase)
                .findAny().orElseThrow(() -> new ServerErrorException(BASE_CURRENCY_ERROR_MESSAGE));

        String currencySymbols = Arrays.stream(Currency.values()).filter(c -> !c.isBase())
                .map(Currency::getValue).collect(Collectors.joining(","));

        return Map.of("base", currencyBase.getValue(), "symbols", currencySymbols);
    }
}
