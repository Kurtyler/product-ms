package com.collabera.kurt.product.exception;

public class InvalidInputException extends ProductException {

    private static final String DOMAIN = "P";
    private static final String CODE = "002";

    public InvalidInputException(String message) {
        super(DOMAIN, CODE, message);
    }
}
