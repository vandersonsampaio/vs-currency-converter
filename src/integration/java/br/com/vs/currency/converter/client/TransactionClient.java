package br.com.vs.currency.converter.client;

import br.com.vs.currency.converter.resource.dto.request.ConversionRequest;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import org.springframework.http.MediaType;

import static io.restassured.RestAssured.given;

public class TransactionClient {

    private static final String BASE_URI = "/conversion";
    private final RequestSpecification specBuilder;

    public TransactionClient(int port) {
        specBuilder = new RequestSpecBuilder().setPort(port)
                .setContentType(MediaType.APPLICATION_JSON_VALUE)
                .setBaseUri("http://localhost")
                .build().log().all();
    }

    public Response getConversion(String id) {
        return given()
                .pathParams("id", id)
                .spec(specBuilder)
                .when()
                .get(BASE_URI + "/{id}");
    }

    public Response findAll(long userId) {
        return given()
                .pathParams("userId", userId)
                .spec(specBuilder)
                .when()
                .get(BASE_URI + "/user/{userId}");
    }

    public Response converter(ConversionRequest request) {
        return given()
                .spec(specBuilder)
                .with()
                .body(request)
                .when()
                .post(BASE_URI);
    }
}
