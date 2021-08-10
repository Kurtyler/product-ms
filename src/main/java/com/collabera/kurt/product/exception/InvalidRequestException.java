package com.collabera.kurt.product.exception;

public class InvalidRequestException extends ProductException {

    private static final String DOMAIN = "P";
    private static final String CODE = "002";

    public InvalidRequestException(String message) {
        super(DOMAIN, CODE, message);
    }
}
