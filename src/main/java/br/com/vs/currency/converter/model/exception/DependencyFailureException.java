package br.com.vs.currency.converter.model.exception;

public class DependencyFailureException extends RuntimeException {

    public DependencyFailureException(String message) {
        super(message);
    }
}
