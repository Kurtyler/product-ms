package com.collabera.kurt.product.service.impl;

import com.collabera.kurt.product.exception.InvalidRequestException;
import com.collabera.kurt.product.service.RequestValidatorService;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Map;

@Slf4j
@Service
public class RequestValidatorServiceImpl implements RequestValidatorService {

    private final ObjectMapper objectMapper;

    public RequestValidatorServiceImpl(final ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    /**
     * Request Validator Service to validate requests
     * @param object
     * @throws InvalidRequestException
     */
    @Override
    public void validateRequest(final Object object) throws InvalidRequestException {
        Map<String, String> map = objectMapper.convertValue(object,
                new TypeReference<Map<String, String>>() {});
        for (Map.Entry<String, String> entry : map.entrySet()) {
            if (entry.getValue() == null || entry.getValue().isEmpty()) {
                throw new InvalidRequestException("Value cannot be empty for field: " + entry.getKey());
            }
        }
    }
}
