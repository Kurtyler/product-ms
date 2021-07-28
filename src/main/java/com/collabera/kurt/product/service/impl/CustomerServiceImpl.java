package com.collabera.kurt.product.service.impl;

import com.collabera.kurt.product.dto.request.CustomerRequest;
import com.collabera.kurt.product.dto.response.CustomerResponse;
import com.collabera.kurt.product.entity.Customer;
import com.collabera.kurt.product.exception.NotFoundException;
import com.collabera.kurt.product.repository.CustomerRepository;
import com.collabera.kurt.product.service.CustomerService;
import com.collabera.kurt.product.service.KafkaProducerService;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class CustomerServiceImpl implements CustomerService {

    private final CustomerRepository customerRepository;

    private final KafkaProducerService kafkaProducerService;

    public CustomerServiceImpl(final CustomerRepository customerRepository,
                               final KafkaProducerService kafkaProducerService) {
        this.customerRepository = customerRepository;
        this.kafkaProducerService = kafkaProducerService;
    }

    /**
     * Customer Service to add product
     * @param customerRequest
     * @return
     */
    @Override
    public CustomerResponse addCustomer(final CustomerRequest customerRequest) {
        CustomerResponse customerResponse = new CustomerResponse();
        try {
            kafkaProducerService.publishToTopic("Attempting to add customer with request: " + customerRequest);
            customerResponse = new CustomerResponse(
                    customerRepository.save(Customer.builder()
                            .name(customerRequest.getName())
                            .email(customerRequest.getEmail())
                            .gender(customerRequest.getGender())
                            .build()));
            kafkaProducerService.publishToTopic("Attempting added customer with response: " + customerResponse);

        } catch (final Exception exception) {
            kafkaProducerService.publishToTopic("Failed to save customer with error: " + exception.getMessage());
        }
        return customerResponse;
    }

    /**
     * Customer Service to fetch customer by Customer Id
     * @param customerId
     * @return
     * @throws NotFoundException
     */
    @Override
    public CustomerResponse getCustomerById(final Integer customerId) throws NotFoundException {
        final CustomerResponse customerResponse = new CustomerResponse();
        try {
            kafkaProducerService.publishToTopic("Attempting to fetch customer with customerId: " + customerId);
            final Optional<Customer> customerData = customerRepository.findById(customerId);
            if (customerData.isPresent()) {
                customerResponse.setCustomerId(customerData.get().getCustomerId());
                customerResponse.setName(customerData.get().getName());
                customerResponse.setEmail(customerData.get().getEmail());
                customerResponse.setGender(customerData.get().getGender());
                kafkaProducerService.publishToTopic("Successfully fetched customer with response: " + customerResponse);

            } else {
                kafkaProducerService.publishToTopic(
                        "Failed to fetch customer with error: Customer not found with Id: " + customerId);
                throw new NotFoundException("Customer not found with Id: " + customerId);
            }

        } catch (final Exception exception) {
            throw new NotFoundException(exception.getMessage());
        }
        return customerResponse;
    }

    /**
     * Customer Service to fetch all Customers
     * @return
     */
    @Override
    public List<CustomerResponse> getCustomers() {
        kafkaProducerService.publishToTopic("Attempting to fetch all customers");
        final List<Customer> customers = customerRepository.findAll();
        final List<CustomerResponse> customerResponses = new ArrayList<>();
        customers.forEach(customer -> customerResponses.add(new CustomerResponse(customer)));
        kafkaProducerService.publishToTopic("Successfully fetched all customers: " + customerResponses);
        return customerResponses;
    }

    /**
     * Customer Service to update customer by Customer Id
     * @param customerRequest
     * @param customerId
     * @return
     * @throws NotFoundException
     */
    @Override
    public CustomerResponse updateCustomer(final CustomerRequest customerRequest, final Integer customerId)
            throws NotFoundException {
        CustomerResponse customerResponse;
        try {
            kafkaProducerService.publishToTopic(
                    "Attempting to update product with request: " + customerRequest.toString());
            this.getCustomerById(customerId);
            customerResponse = new CustomerResponse(
                    customerRepository.save(Customer.builder()
                            .customerId(customerId)
                            .name(customerRequest.getName())
                            .email(customerRequest.getEmail())
                            .gender(customerRequest.getGender())
                            .build()));
            kafkaProducerService.publishToTopic(
                    "Successfully updated customer with response: " + customerResponse.toString());

        } catch (final Exception exception) {
            kafkaProducerService.publishToTopic("Failed to update customer with error: " + exception.getMessage());
            throw new NotFoundException(exception.getMessage());
        }
        return customerResponse;
    }
}
