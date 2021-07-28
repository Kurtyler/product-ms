package com.collabera.kurt.product.exception;

public class InternalServerErrorException extends ProductException {

    private static final String DOMAIN = "P";
    private static final String CODE = "000";

    public InternalServerErrorException(final String message) {
        super(
                DOMAIN,
                CODE,
                message
        );
    }
}
