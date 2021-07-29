package com.collabera.kurt.product.service;

import com.collabera.kurt.product.exception.InvalidInputException;

public interface RequestValidatorService {

    void validateRequest(Object object) throws InvalidInputException;
}
