package com.xyz.vendingmachine.machine;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Builder.Default;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.validation.ObjectError;

import javax.validation.ConstraintViolation;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Default error message
 * @author amarenco
 */
@Data
@Builder(builderClassName = "Builder")
@AllArgsConstructor
@NoArgsConstructor
public class ErrorMessage {
    private List<String> messages;
    private int status;

    @Default
    private LocalDateTime timestamp = LocalDateTime.now();

    /**
     * Builder pattern for the {@link ErrorMessage}
     * @author amarenco
     */
    public static class Builder {
        /**
         * @param message the message for the error
         * @return the current builder
         */
        public ErrorMessage.Builder message(String message) {
            if (this.messages == null) {
                this.messages = new ArrayList<>();
            }

            this.messages.add(message);
            return this;
        }

        /**
         * @param errors the list of errors
         * @return the current builder
         */
        public ErrorMessage.Builder message(Collection<?> errors) {
            if (this.messages == null) {
                this.messages = new ArrayList<>();
            }

            errors.forEach(error -> {
                if (error instanceof ObjectError) {
                    this.messages.add(((ObjectError)error).getDefaultMessage());
                } else if (error instanceof ConstraintViolation) {
                    this.messages.add(((ConstraintViolation)error).getMessage());
                }
            });
            return this;
        }


        /**
         * @param status the HTTP status for the error
         * @return the current builder
         */
        public ErrorMessage.Builder status(HttpStatus status) {
            this.status = status.value();
            return this;
        }
    }
}
