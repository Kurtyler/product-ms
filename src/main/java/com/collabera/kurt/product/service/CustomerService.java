package com.collabera.kurt.product.service;

import com.collabera.kurt.product.dto.request.CustomerRequest;
import com.collabera.kurt.product.dto.response.CustomerResponse;
import com.collabera.kurt.product.exception.InvalidInputException;
import com.collabera.kurt.product.exception.NotFoundException;

import java.util.List;

public interface CustomerService {

    CustomerResponse addCustomer(CustomerRequest customerRequest) throws InvalidInputException;

    CustomerResponse getCustomerById(Integer customerId) throws NotFoundException;

    List<CustomerResponse> getCustomers();

    CustomerResponse updateCustomer(CustomerRequest customerRequest, Integer customerId)
            throws NotFoundException, InvalidInputException;
}
