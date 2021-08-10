package com.collabera.kurt.product.service;

import com.collabera.kurt.product.exception.InvalidRequestException;

public interface RequestValidatorService {

    void validateRequest(Object object) throws InvalidRequestException;
}
