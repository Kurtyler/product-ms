package com.collabera.kurt.product.controller;

import com.collabera.kurt.product.dto.request.OrderRequest;
import com.collabera.kurt.product.dto.response.OrderResponse;
import com.collabera.kurt.product.exception.InvalidInputException;
import com.collabera.kurt.product.exception.InvalidOrderException;
import com.collabera.kurt.product.exception.NotFoundException;
import com.collabera.kurt.product.service.OrderService;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/order")
public class OrderController {

    private final OrderService orderService;

    @Autowired
    public OrderController(final OrderService orderService) {
        this.orderService = orderService;
    }

    @Operation(summary = "This is to add order")
    @PostMapping("/addOrder")
    public ResponseEntity<OrderResponse> addOrder(@RequestBody final OrderRequest orderRequest)
            throws NotFoundException, InvalidInputException, InvalidOrderException {
        return ResponseEntity.status(HttpStatus.OK).body(orderService.addOrder(orderRequest));
    }

    @Operation(summary = "This is to fetch order by Order Id")
    @GetMapping("/getOrderById/{orderId}")
    public ResponseEntity<OrderResponse> getOrderById(@PathVariable("orderId") final Integer orderId)
            throws NotFoundException {
        return ResponseEntity.ok(orderService.getOrderById(orderId));
    }

    @Operation(summary = "This is to accept order by Order Id")
    @PutMapping("/acceptOrderById/{orderId}")
    public ResponseEntity<OrderResponse> acceptOrderById(@PathVariable("orderId") final Integer orderId)
            throws NotFoundException, InvalidOrderException {
        return ResponseEntity.status(HttpStatus.OK).body(orderService.acceptOrderById(orderId));
    }

    @Operation(summary = "This is to accept all order/s by Customer Id")
    @PutMapping("/acceptOrdersByCustomerId/{customerId}")
    public ResponseEntity<List<OrderResponse>> acceptOrderByCustomerId(
            @PathVariable("customerId") final Integer customerId) throws NotFoundException {
        return ResponseEntity.status(HttpStatus.OK).body(orderService.acceptOrderByCustomerId(customerId));
    }

    @Operation(summary = "This is to fetch all order/s by Customer Id")
    @GetMapping("/getOrdersByCustomerId/{customerId}")
    public ResponseEntity<List<OrderResponse>> getOrderByCustomerId(
            @PathVariable("customerId") final Integer customerId) throws NotFoundException {
        return ResponseEntity.status(HttpStatus.OK).body(orderService.getOrderByCustomerId(customerId));
    }

}
