package com.xyz.vendingmachine.server;

import com.xyz.vendingmachine.machine.AbstractExceptionHandler;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.client.HttpClientErrorException;

/**
 * Default exception handler for REST controllers
 * @author amarenco
 */
@ControllerAdvice
public class DefaultExceptionHandler extends AbstractExceptionHandler {

    @ExceptionHandler({
            HttpClientErrorException.class,
            MissingServletRequestParameterException.class
    })
    @Override
    public ResponseEntity<?> handleBadRequestException(Exception ex) {
        return super.handleBadRequestException(ex);
    }
}
