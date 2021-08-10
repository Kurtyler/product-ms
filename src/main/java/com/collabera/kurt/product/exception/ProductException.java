package com.collabera.kurt.product.exception;

public class ProductException extends Exception{
    private final String domain;
    private final String errorCode;

    public ProductException(
            final String domain,
            final String errorCode,
            final String message
    ) {
        super(message);
        this.domain = domain;
        this.errorCode = errorCode;
    }

    public final String toCode() {
        return this.domain + "." + this.errorCode;
    }
}
