package com.collabera.kurt.product.exception;

public class InvalidOrderException extends ProductException {

    private static final String DOMAIN = "P";
    private static final String CODE = "003";

    public InvalidOrderException(String message) {
        super(DOMAIN, CODE, message);
    }
}
