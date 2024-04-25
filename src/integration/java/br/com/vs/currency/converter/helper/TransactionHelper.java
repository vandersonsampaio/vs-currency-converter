package br.com.vs.currency.converter.helper;

import br.com.vs.currency.converter.AppIT;
import br.com.vs.currency.converter.client.TransactionClient;
import br.com.vs.currency.converter.model.entity.Conversion;
import br.com.vs.currency.converter.model.enums.Currency;
import br.com.vs.currency.converter.model.exception.ErrorResponse;
import br.com.vs.currency.converter.model.exception.SimpleErrorResponse;
import br.com.vs.currency.converter.model.repository.ConversionRepository;
import br.com.vs.currency.converter.resource.dto.request.ConversionRequest;
import br.com.vs.currency.converter.resource.dto.response.ConversionResponse;
import io.restassured.response.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.CacheManager;
import org.springframework.http.MediaType;
import wiremock.org.eclipse.jetty.http.HttpStatus;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;

import static br.com.vs.currency.converter.config.CacheConfig.CACHE_NAME;
import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.get;
import static com.github.tomakehurst.wiremock.client.WireMock.stubFor;
import static com.github.tomakehurst.wiremock.client.WireMock.urlMatching;

public class TransactionHelper extends AppIT {

    private static final String CONTENT_TYPE = "Content-Type";
    @Value("${exchange.server.endpoints.get}")
    private String getRate;

    @Autowired
    private ConversionRepository repository;
    @Autowired
    private CacheManager cacheManager;

    private TransactionClient client;

    protected void init() {
        client = new TransactionClient(port);
        cacheManager.getCache(CACHE_NAME).clear();
        wireMockServer.resetAll();
    }

    protected void externalApiSuccess() {
        stubFor(get(urlMatching(getRate + "?.*"))
                .willReturn(aResponse()
                        .withStatus(HttpStatus.OK_200)
                        .withHeader(CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                        .withBodyFile("success.json")));

    }

    protected void externalApiInternalError() {
        stubFor(get(urlMatching(getRate + "?.*"))
                .willReturn(aResponse()
                        .withStatus(HttpStatus.INTERNAL_SERVER_ERROR_500)
                        .withHeader(CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                        .withBodyFile("internal-error.json")));

    }

    protected void externalApiBadRequest() {
        stubFor(get(urlMatching(getRate + "?.*"))
                .willReturn(aResponse()
                        .withStatus(HttpStatus.BAD_REQUEST_400)
                        .withHeader(CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                        .withBodyFile("bad-request.json")));

    }

    protected void externalApiUnauthorized() {
        stubFor(get(urlMatching(getRate + "?.*"))
                .willReturn(aResponse()
                        .withStatus(HttpStatus.UNAUTHORIZED_401)
                        .withHeader(CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                        .withBodyFile("unauthorized.json")));

    }

    protected void externalApiNotFound() {
        stubFor(get(urlMatching(getRate + "?.*"))
                .willReturn(aResponse()
                        .withStatus(HttpStatus.NOT_FOUND_404)
                        .withHeader(CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                        .withBodyFile("not-found.json")));

    }

    protected void externalApiManyRequest() {
        stubFor(get(urlMatching(getRate + "?.*"))
                .willReturn(aResponse()
                        .withStatus(HttpStatus.TOO_MANY_REQUESTS_429)
                        .withHeader(CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                        .withBodyFile("many-request.json")));

    }

    protected String createConversion(Long userId, Currency sourceCurrency, Currency targetCurrency,
                                      BigDecimal amount) {
        BigDecimal sourceRate = BigDecimal.valueOf(1.07);
        BigDecimal targetRate = BigDecimal.valueOf(3.79);

        return repository.save(buildConversion(userId, sourceCurrency, targetCurrency, amount,
                sourceRate, targetRate)).getId();
    }

    private Conversion buildConversion(Long userId, Currency sourceCurrency, Currency targetCurrency,
                                         BigDecimal amount, BigDecimal sourceRate, BigDecimal targetRate) {
        var conversion = Conversion.builder().userId(userId).sourceCurrency(sourceCurrency)
                .targetCurrency(targetCurrency).sourceAmount(amount).build();
        conversion.generateId();
        conversion.calculateTarget(sourceRate, targetRate);

        return conversion;
    }

    protected ConversionResponse getConversionById(String id) {
        Response response = client.getConversion(id);

        response.then().statusCode(HttpStatus.OK_200);

        return response.as(ConversionResponse.class);
    }

    protected SimpleErrorResponse getConversionByIdNotFound(String id) {
        Response response = client.getConversion(id);

        response.then().statusCode(HttpStatus.NOT_FOUND_404);

        return response.as(SimpleErrorResponse.class);
    }

    protected ErrorResponse getConversionByIdBadRequest(String id) {
        Response response = client.getConversion(id);

        response.then().statusCode(HttpStatus.BAD_REQUEST_400);

        return response.as(ErrorResponse.class);
    }

    protected List<ConversionResponse> findAllByUserId(Long userId) {
        Response response = client.findAll(userId);

        response.then().statusCode(HttpStatus.OK_200);

        return Arrays.stream(response.as(ConversionResponse[].class)).toList();
    }

    protected ErrorResponse findAllByUserIdBadRequest(Long userId) {
        Response response = client.findAll(userId);

        response.then().statusCode(HttpStatus.BAD_REQUEST_400);

        return response.as(ErrorResponse.class);
    }

    protected ConversionResponse converter(Long userId, Currency source, Currency target, BigDecimal amount) {
        var request = new ConversionRequest(userId, source, amount, target);

        Response response = client.converter(request);

        response.then().statusCode(HttpStatus.CREATED_201);

        return response.as(ConversionResponse.class);
    }

    protected ErrorResponse converterBadRequest(Long userId, Currency source, Currency target, BigDecimal amount) {
        var request = new ConversionRequest(userId, source, amount, target);

        Response response = client.converter(request);

        response.then().statusCode(HttpStatus.BAD_REQUEST_400);

        return response.as(ErrorResponse.class);
    }

    protected SimpleErrorResponse converterFailedDependency(Long userId, Currency source,
                                                      Currency target, BigDecimal amount) {
        var request = new ConversionRequest(userId, source, amount, target);

        Response response = client.converter(request);

        response.then().statusCode(HttpStatus.FAILED_DEPENDENCY_424);

        return response.as(SimpleErrorResponse.class);
    }
}
