package com.xyz.vendingmachine.machine;

import com.xyz.vendingmachine.machine.exception.ClosedMachineException;
import com.xyz.vendingmachine.machine.exception.InvalidSaleException;
import com.xyz.vendingmachine.machine.exception.LockedMachineException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

/**
 * Default exception handler for REST controllers
 * @author amarenco
 */
@ControllerAdvice
public class DefaultExceptionHandler extends AbstractExceptionHandler {

    @ExceptionHandler({
            LockedMachineException.class,
            ClosedMachineException.class,
            InvalidSaleException.class,
            MissingServletRequestParameterException.class
    })
    @Override
    public ResponseEntity<?> handleBadRequestException(Exception ex) {
        return super.handleBadRequestException(ex);
    }
}
