package com.collabera.kurt.product.service.impl;

import com.collabera.kurt.product.dto.request.CustomerRequest;
import com.collabera.kurt.product.dto.response.CustomerResponse;
import com.collabera.kurt.product.entity.Customer;
import com.collabera.kurt.product.enums.CustomerMessage;
import com.collabera.kurt.product.exception.InvalidRequestException;
import com.collabera.kurt.product.exception.NotFoundException;
import com.collabera.kurt.product.repository.CustomerRepository;
import com.collabera.kurt.product.service.CustomerService;
import com.collabera.kurt.product.service.KafkaProducerService;
import com.collabera.kurt.product.service.RequestValidatorService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CustomerServiceImpl implements CustomerService {

    private final CustomerRepository customerRepository;
    private final KafkaProducerService kafkaProducerService;
    private final RequestValidatorService requestValidatorService;

    /**
     * Customer Service to add product
     * @param customerRequest
     * @return
     */
    @Override
    public CustomerResponse addCustomer(final CustomerRequest customerRequest) throws InvalidRequestException {
        CustomerResponse customerResponse;
        try {
            kafkaProducerService.publishToTopic(CustomerMessage.SAVING_CUSTOMER.getDescription() + customerRequest);
            requestValidatorService.validateRequest(customerRequest);
            customerResponse = new CustomerResponse(
                    customerRepository.save(Customer.builder()
                            .name(customerRequest.getName())
                            .email(customerRequest.getEmail())
                            .gender(customerRequest.getGender())
                            .build()));
            kafkaProducerService.publishToTopic(CustomerMessage.SAVED_CUSTOMER.getDescription() + customerResponse);

        } catch (final Exception exception) {
            kafkaProducerService.publishToTopic(
                    CustomerMessage.FAILED_SAVING_CUSTOMER.getDescription() + exception.getMessage());
            throw new InvalidRequestException(exception.getMessage());
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
            kafkaProducerService.publishToTopic(CustomerMessage.FETCHING_CUSTOMER.getDescription());
            final Optional<Customer> customerData = customerRepository.findById(customerId);
            if (!customerData.isPresent()) {
                throw new NotFoundException(CustomerMessage.CUSTOMER_NOT_FOUND.getDescription() + customerId);
            }

            customerResponse.setCustomerId(customerData.get().getCustomerId());
            customerResponse.setName(customerData.get().getName());
            customerResponse.setEmail(customerData.get().getEmail());
            customerResponse.setGender(customerData.get().getGender());
            kafkaProducerService.publishToTopic(
                    CustomerMessage.FETCHED_CUSTOMER.getDescription() + customerResponse);

        } catch (final Exception exception) {
            kafkaProducerService.publishToTopic(
                    CustomerMessage.FAILED_FETCHING_CUSTOMER.getDescription() + exception.getMessage());
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
        kafkaProducerService.publishToTopic(CustomerMessage.FETCHING_CUSTOMER.getDescription());
        final List<Customer> customers = customerRepository.findAll();
        final List<CustomerResponse> customerResponses = new ArrayList<>();
        customers.forEach(customer -> customerResponses.add(new CustomerResponse(customer)));
        kafkaProducerService.publishToTopic(CustomerMessage.FETCHED_CUSTOMER.getDescription() + customerResponses);
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
            throws NotFoundException, InvalidRequestException {
        CustomerResponse customerResponse;
        try {
            kafkaProducerService.publishToTopic(
                    CustomerMessage.UPDATING_CUSTOMER.getDescription() + customerRequest.toString());
            this.getCustomerById(customerId);
            requestValidatorService.validateRequest(customerRequest);
            customerResponse = new CustomerResponse(
                    customerRepository.save(Customer.builder()
                            .customerId(customerId)
                            .name(customerRequest.getName())
                            .email(customerRequest.getEmail())
                            .gender(customerRequest.getGender())
                            .build()));
            kafkaProducerService.publishToTopic(CustomerMessage.UPDATED_CUSTOMER.getDescription() + customerResponse);
        } catch(NotFoundException notFoundException){
            kafkaProducerService.publishToTopic(
                    CustomerMessage.FAILED_UPDATING_CUSTOMER.getDescription() + notFoundException.getMessage());
            throw new NotFoundException(notFoundException.getMessage());

        } catch (InvalidRequestException invalidRequestException) {
            kafkaProducerService.publishToTopic(
                    CustomerMessage.FAILED_UPDATING_CUSTOMER.getDescription() + invalidRequestException.getMessage());
            throw new InvalidRequestException(invalidRequestException.getMessage());
        }

        return customerResponse;
    }
}
