package com.mitrais.cdc.covid19backend.exceptions;

import com.mitrais.cdc.covid19backend.controller.ControllerException;
import org.hibernate.service.spi.ServiceException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.oauth2.common.exceptions.InvalidGrantException;
import org.springframework.security.oauth2.common.exceptions.UnauthorizedUserException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.server.ServerErrorException;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import com.mitrais.cdc.covid19backend.utility.Utility;

import javax.persistence.EntityExistsException;
import javax.persistence.EntityNotFoundException;
import javax.validation.constraints.Null;

/**
 * @author Rai Suardhyana Arijasa on 6/9/2020.
 */
@ControllerAdvice
public class CustomExceptionHandler extends ResponseEntityExceptionHandler {

    @ExceptionHandler(ServiceException.class)
    public ResponseEntity handleServiceException(final ServiceException exception) {
        return basicResponse(HttpStatus.BAD_REQUEST, exception.getLocalizedMessage());
    }

    @ExceptionHandler(InvalidGrantException.class)
    public ResponseEntity unAuthorizedException(final InvalidGrantException exception) {
        return basicResponse(HttpStatus.UNAUTHORIZED, exception.getLocalizedMessage());
    }

    @ExceptionHandler(NullPointerException.class)
    public ResponseEntity nullPointerException(final NullPointerException exception) {
        return basicResponse(HttpStatus.INTERNAL_SERVER_ERROR, exception.getLocalizedMessage());
    }

    @ExceptionHandler(ServerErrorException.class)
    public ResponseEntity serverErrorException(final ServerErrorException exception) {
        return basicResponse(HttpStatus.INTERNAL_SERVER_ERROR, exception.getLocalizedMessage());
    }

    @ExceptionHandler(EntityExistsException.class)
    public ResponseEntity serverErrorException(final EntityExistsException exception) {
        return basicResponse(HttpStatus.INTERNAL_SERVER_ERROR, exception.getLocalizedMessage());
    }

    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity entityNotFoundException(final EntityNotFoundException exception) {
        return basicResponse(HttpStatus.INTERNAL_SERVER_ERROR, exception.getLocalizedMessage());
    }

    @ExceptionHandler(ControllerException.class)
    public ResponseEntity badRequestException(final ControllerException exception) {
        return basicResponse(HttpStatus.BAD_REQUEST, exception.getLocalizedMessage());
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity handleAbstractException(final Exception exception) {
        return basicResponse(HttpStatus.BAD_REQUEST, exception.getLocalizedMessage());
    }

    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException exception,
                                                                  HttpHeaders headers, HttpStatus status, WebRequest request) {
        StringBuilder errorBuilder = new StringBuilder();
        exception.getBindingResult().getAllErrors().forEach((error) -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            errorBuilder.append(fieldName).append(" ").append(errorMessage).append("\n");
        });
        return new ResponseEntity<>(errorBuilder.toString(), HttpStatus.BAD_REQUEST);
    }

    private ResponseEntity basicResponse(HttpStatus status, String message) {
        return ResponseEntity.status(status).body(new Utility(message, null)
        );
    }

}
