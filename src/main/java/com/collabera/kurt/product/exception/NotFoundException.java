package com.collabera.kurt.product.exception;

public class NotFoundException extends ProductException {

    private static final String DOMAIN = "P";
    private static final String CODE = "001";

    public NotFoundException(String message) {
        super(DOMAIN, CODE, message);
    }

}
