package br.com.vs.currency.converter.model.exception;

public class TooManyRequestException extends RuntimeException {

    public TooManyRequestException(String message) {
        super(message);
    }
}
