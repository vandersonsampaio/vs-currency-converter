package br.com.vs.currency.converter.config;

import br.com.vs.currency.converter.model.exception.DependencyFailureException;
import br.com.vs.currency.converter.model.exception.ErrorResponse;
import br.com.vs.currency.converter.model.exception.NotFoundException;
import br.com.vs.currency.converter.model.exception.ServerErrorException;
import br.com.vs.currency.converter.model.exception.SimpleErrorResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.MessageSourceResolvable;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.method.ParameterValidationResult;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.method.annotation.HandlerMethodValidationException;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.util.stream.Collectors;

@Slf4j
@RestControllerAdvice
public class RestExceptionHandler extends ResponseEntityExceptionHandler {

    @ExceptionHandler({ Exception.class, ServerErrorException.class })
    protected ResponseEntity<Object> handleInternalServerError(Exception ex) {
        log.error(ex.getMessage(), ex);
        return buildResponseEntity(new ErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR, ex));
    }

    @ExceptionHandler({ NotFoundException.class })
    protected ResponseEntity<Object> handleNotFound(Exception ex) {
        log.error(ex.getMessage());

        return new ResponseEntity<>(new SimpleErrorResponse(ex.getMessage()), HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler({ DependencyFailureException.class })
    protected ResponseEntity<Object> handleDependencyFailure(Exception ex) {
        log.error(ex.getMessage());

        return new ResponseEntity<>(new SimpleErrorResponse(ex.getMessage()), HttpStatus.FAILED_DEPENDENCY);
    }

    private ResponseEntity<Object> buildResponseEntity(ErrorResponse apiError) {
        return new ResponseEntity<>(apiError, apiError.getStatus());
    }

    @Override
    protected ResponseEntity<Object> handleHandlerMethodValidationException(
            HandlerMethodValidationException ex, HttpHeaders headers, HttpStatusCode status, WebRequest request) {
        log.error(ex.getMessage(), ex);

        String message = ex.getAllValidationResults().stream()
                .map(ParameterValidationResult::getResolvableErrors)
                .map(a -> a.stream()
                        .map(MessageSourceResolvable::getDefaultMessage).collect(Collectors.joining("\r\n")))
                .collect(Collectors.joining("\r\n"));

        return buildResponseEntity(new ErrorResponse(HttpStatus.BAD_REQUEST, message));
    }

    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(
            MethodArgumentNotValidException ex, HttpHeaders headers, HttpStatusCode status, WebRequest request) {
        log.error(ex.getMessage(), ex);

        String message = ex.getAllErrors().stream()
                .map(DefaultMessageSourceResolvable::getDefaultMessage)
                .collect(Collectors.joining("\r\n"));

        return buildResponseEntity(new ErrorResponse(HttpStatus.valueOf(status.value()), message));
    }
}
