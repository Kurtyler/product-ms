package com.collabera.kurt.product.controller;

import com.collabera.kurt.product.dto.request.OrderRequest;
import com.collabera.kurt.product.dto.response.OrderResponse;
import com.collabera.kurt.product.exception.InvalidInputException;
import com.collabera.kurt.product.exception.InvalidOrderException;
import com.collabera.kurt.product.exception.NotFoundException;
import com.collabera.kurt.product.service.OrderService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/order")
public class OrderController {

    private final OrderService orderService;

    public OrderController(final OrderService orderService) {
        this.orderService = orderService;
    }

    @PostMapping("/addOrder")
    public ResponseEntity<OrderResponse> addOrder(@RequestBody final OrderRequest orderRequest)
            throws NotFoundException, InvalidInputException, InvalidOrderException {
        return ResponseEntity.status(HttpStatus.OK).body(orderService.addOrder(orderRequest));
    }

    @GetMapping("/getOrderById/{orderId}")
    public ResponseEntity<OrderResponse> getOrderById(@PathVariable("orderId") final Integer orderId)
            throws NotFoundException {
        return ResponseEntity.ok(orderService.getOrderById(orderId));
    }

    @PutMapping("/acceptOrderById/{orderId}")
    public ResponseEntity<OrderResponse> acceptOrderById(@PathVariable("orderId") final Integer orderId)
            throws NotFoundException, InvalidOrderException {
        return ResponseEntity.status(HttpStatus.OK).body(orderService.acceptOrderById(orderId));
    }

    @PutMapping("/acceptOrderByCustomerId/{customerId}")
    public ResponseEntity<List<OrderResponse>> acceptOrderByCustomerId(
            @PathVariable("customerId") final Integer customerId) throws NotFoundException {
        return ResponseEntity.status(HttpStatus.OK).body(orderService.acceptOrderByCustomerId(customerId));
    }

    @GetMapping("/getOrderByCustomerId/{customerId}")
    public ResponseEntity<List<OrderResponse>> getOrderByCustomerId(
            @PathVariable("customerId") final Integer customerId) throws NotFoundException {
        return ResponseEntity.status(HttpStatus.OK).body(orderService.getOrderByCustomerId(customerId));
    }

}
