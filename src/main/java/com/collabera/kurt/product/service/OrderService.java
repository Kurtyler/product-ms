package com.collabera.kurt.product.service;

import com.collabera.kurt.product.dto.request.OrderRequest;
import com.collabera.kurt.product.dto.response.OrderResponse;
import com.collabera.kurt.product.exception.InvalidInputException;
import com.collabera.kurt.product.exception.InvalidOrderException;
import com.collabera.kurt.product.exception.NotFoundException;

import java.util.List;

public interface OrderService {

    OrderResponse addOrder(OrderRequest orderRequest) throws NotFoundException, InvalidInputException, InvalidOrderException;

    OrderResponse getOrderById(Integer orderId) throws NotFoundException;

    List<OrderResponse> getOrderByCustomerId(Integer customerId) throws NotFoundException;

    OrderResponse acceptOrderById(Integer orderId) throws NotFoundException, InvalidOrderException;

    List<OrderResponse> acceptOrderByCustomerId(Integer customerId) throws NotFoundException;
}
