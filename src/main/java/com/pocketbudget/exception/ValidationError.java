package com.pocketbudget.exception;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = false)
@AllArgsConstructor
public class ValidationError {
    private String field;
    private Object rejectedValue;
    private String message;

    public ValidationError(String message) {
        this.message = message;
    }
}
