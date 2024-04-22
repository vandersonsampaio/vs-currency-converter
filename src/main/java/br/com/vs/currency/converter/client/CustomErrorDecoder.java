package br.com.vs.currency.converter.client;

import br.com.vs.currency.converter.model.exception.BadRequestException;
import br.com.vs.currency.converter.model.exception.GenericErrorException;
import br.com.vs.currency.converter.model.exception.NotFoundException;
import br.com.vs.currency.converter.model.exception.ServerErrorException;
import br.com.vs.currency.converter.model.exception.TooManyRequestException;
import br.com.vs.currency.converter.model.exception.UnauthorizedException;
import feign.Response;
import feign.Util;
import feign.codec.ErrorDecoder;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

public class CustomErrorDecoder implements ErrorDecoder {

    @Override
    public Exception decode(String methodKey, Response response) {
        String body;

        try {
            body = Util.toString(response.body().asReader(StandardCharsets.UTF_8));
        } catch (IOException e) {
            throw new GenericErrorException(e);
        }

        return switch (response.status()) {
            case 400 -> new BadRequestException(body);
            case 401 -> new UnauthorizedException(body);
            case 404 -> new NotFoundException(body);
            case 429 -> new TooManyRequestException(body);
            case 500, 502, 503, 504 -> new ServerErrorException(body);
            default -> new GenericErrorException();
        };
    }
}
