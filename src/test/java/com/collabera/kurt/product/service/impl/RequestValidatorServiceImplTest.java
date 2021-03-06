package com.collabera.kurt.product.service.impl;

import com.collabera.kurt.product.dto.request.OrderRequest;
import com.collabera.kurt.product.exception.InvalidRequestException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;


@ExtendWith(MockitoExtension.class)
class RequestValidatorServiceImplTest {

    @InjectMocks
    RequestValidatorServiceImpl requestValidatorService;

    @Mock
    ObjectMapper objectMapper;

    @Test
    void validateRequest() throws InvalidRequestException {

        Map<String, String> map = new ConcurrentHashMap<>();
        map.put("customerId", "1");
        map.put("productId", "1");
        map.put("quantity", "1");

        when(objectMapper.convertValue(any(), any(TypeReference.class))).thenReturn(map);
        requestValidatorService.validateRequest(new OrderRequest());
    }

    @Test
    void validateRequestThrowsInvalidInputException() {

        Map<String, String> map = new ConcurrentHashMap<>();
        map.put("customerId", "1");
        map.put("productId", "");
        map.put("quantity", "1");

        when(objectMapper.convertValue(any(), any(TypeReference.class))).thenReturn(map);

        assertThrows(InvalidRequestException.class, () -> requestValidatorService.validateRequest(new OrderRequest()));
    }
}