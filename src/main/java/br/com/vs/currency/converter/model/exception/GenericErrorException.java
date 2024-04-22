package br.com.vs.currency.converter.model.exception;

public class GenericErrorException extends RuntimeException {

    private static final String MESSAGE = "Generic Error";

    public GenericErrorException() {
        super(MESSAGE);
    }

    public GenericErrorException(Exception ex) {
        super(MESSAGE, ex);
    }
}
