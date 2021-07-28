package com.collabera.kurt.product.controller;

import com.collabera.kurt.product.dto.request.CustomerRequest;
import com.collabera.kurt.product.dto.response.CustomerResponse;
import com.collabera.kurt.product.exception.NotFoundException;
import com.collabera.kurt.product.service.CustomerService;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/customer")
public class CustomerController {

    private final CustomerService customerService;

    @Autowired
    public CustomerController(final CustomerService customerService) {
        this.customerService = customerService;
    }

    @Operation(summary = "This is to add customer")
    @PostMapping("/addCustomer")
    public ResponseEntity<CustomerResponse> addCustomer(@RequestBody final CustomerRequest customerRequest) {
        return ResponseEntity.status(HttpStatus.OK).body(customerService.addCustomer(customerRequest));
    }

    @Operation(summary = "This is to fetch all customers")
    @GetMapping("/getCustomers")
    public ResponseEntity<List<CustomerResponse>> getCustomerById() {
        return ResponseEntity.status(HttpStatus.OK).body(customerService.getCustomers());
    }

    @Operation(summary = "This is to update customer by Customer Id")
    @PutMapping("/updateCustomer/{customerId}")
    public ResponseEntity<CustomerResponse> updateCustomer(
            @RequestBody final CustomerRequest customerRequest,
            @PathVariable("customerId") final Integer customerId) throws NotFoundException {
        return ResponseEntity.status(HttpStatus.OK).body(customerService.updateCustomer(customerRequest, customerId));
    }

    @Operation(summary = "This is to fetch customer by Customer Id")
    @GetMapping("/getCustomerById/{customerId}")
    public ResponseEntity<CustomerResponse> getCustomerById(@PathVariable("customerId") final Integer customerId)
            throws NotFoundException {
        return ResponseEntity.status(HttpStatus.OK).body(customerService.getCustomerById(customerId));
    }
}
