package br.com.vs.currency.converter.client;

import br.com.vs.currency.converter.config.FeignClientConfig;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Map;

@FeignClient(name = "exchangeRateClient", url = "${exchange.server.uri}", configuration = FeignClientConfig.class)
@Retry(name = "exchangeRateClient")
@CircuitBreaker(name = "exchangeRateClient")
public interface ExchangeRateClient {

    @GetMapping(path = "${exchange.server.endpoints.get}",
            produces = MediaType.APPLICATION_JSON_VALUE)
    ResponseEntity<?> latest(@RequestParam Map<String, String> queryParams);
}
