package com.collabera.kurt.product.controller;

import com.collabera.kurt.product.dto.request.CustomerRequest;
import com.collabera.kurt.product.dto.response.CustomerResponse;
import com.collabera.kurt.product.exception.NotFoundException;
import com.collabera.kurt.product.service.CustomerService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/customer")
public class CustomerController {

    private final CustomerService customerService;

    public CustomerController(final CustomerService customerService) {
        this.customerService = customerService;
    }

    @PostMapping("/addCustomer")
    public ResponseEntity<CustomerResponse> addCustomer(@RequestBody final CustomerRequest customerRequest) {
        return ResponseEntity.status(HttpStatus.OK).body(customerService.addCustomer(customerRequest));
    }

    @GetMapping("/getCustomers")
    public ResponseEntity<List<CustomerResponse>> getCustomerById() {
        return ResponseEntity.status(HttpStatus.OK).body(customerService.getCustomers());
    }

    @PutMapping("/updateCustomer/{customerId}")
    public ResponseEntity<CustomerResponse> updateCustomer(
            @RequestBody final CustomerRequest customerRequest,
            @PathVariable("customerId") final Integer customerId) throws NotFoundException {
        return ResponseEntity.status(HttpStatus.OK).body(customerService.updateCustomer(customerRequest, customerId));
    }

    @GetMapping("/getCustomerById/{customerId}")
    public ResponseEntity<CustomerResponse> getCustomerById(@PathVariable("customerId") final Integer customerId)
            throws NotFoundException {
        return ResponseEntity.status(HttpStatus.OK).body(customerService.getCustomerById(customerId));
    }
}
