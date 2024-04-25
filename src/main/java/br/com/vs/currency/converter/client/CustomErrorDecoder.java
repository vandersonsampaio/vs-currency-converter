package br.com.vs.currency.converter.client;

import br.com.vs.currency.converter.model.exception.DependencyFailureException;
import br.com.vs.currency.converter.model.exception.GenericErrorException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import feign.Response;
import feign.Util;
import feign.codec.ErrorDecoder;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

public class CustomErrorDecoder implements ErrorDecoder {

    @Override
    public Exception decode(String methodKey, Response response) {
        ObjectMapper objectMapper = new ObjectMapper();
        String name;

        try {
            String body = Util.toString(response.body().asReader(StandardCharsets.UTF_8));
            JsonNode jsonNode = objectMapper.readTree(body);

            name = jsonNode.get("message").asText();
        } catch (IOException e) {
            throw new GenericErrorException(e);
        }

        if (isFamily400(response.status()) || isFamily500(response.status())) {
            return new DependencyFailureException(name);
        } else {
            return new GenericErrorException();
        }
    }

    private boolean isFamily400(int status) {
        return status == 400 || status == 401 ||
                status == 404 || status == 429;
    }

    private boolean isFamily500(int status) {
        return status == 500 || status == 502 ||
                status == 503 || status == 504;
    }
}
