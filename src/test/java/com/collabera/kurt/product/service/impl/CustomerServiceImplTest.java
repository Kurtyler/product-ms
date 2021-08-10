package com.collabera.kurt.product.service.impl;

import com.collabera.kurt.product.dto.request.CustomerRequest;
import com.collabera.kurt.product.dto.response.CustomerResponse;
import com.collabera.kurt.product.entity.Customer;
import com.collabera.kurt.product.exception.InvalidRequestException;
import com.collabera.kurt.product.exception.NotFoundException;
import com.collabera.kurt.product.repository.CustomerRepository;
import com.collabera.kurt.product.service.KafkaProducerService;
import com.collabera.kurt.product.service.RequestValidatorService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.*;

@ExtendWith(SpringExtension.class)
class CustomerServiceImplTest {

    @InjectMocks
    CustomerServiceImpl customerService;

    @Mock
    KafkaProducerService kafkaProducerService;

    @Mock
    RequestValidatorService requestValidatorService;

    @Mock
    CustomerRepository customerRepository;

    @Test
    void addCustomer() throws InvalidRequestException {

        Customer customer = new Customer();
        customer.setCustomerId(1);
        customer.setName("Kurt");
        customer.setEmail("sample@gmail.com");
        customer.setGender("Male");

        when(customerRepository.save(any(Customer.class))).thenReturn(customer);
        doNothing().when(kafkaProducerService).publishToTopic(anyString());
        doNothing().when(requestValidatorService).validateRequest(any());
        CustomerResponse customerResponse = customerService.addCustomer(new CustomerRequest());

        assertEquals(1, customerResponse.getCustomerId());
        assertEquals("Kurt",customerResponse.getName());
        assertEquals("sample@gmail.com",customerResponse.getEmail());
        assertEquals("Male",customerResponse.getGender());

    }

    @Test()
    void throwException() throws InvalidRequestException {
        assertThrows(InvalidRequestException.class, () -> customerService.addCustomer(new CustomerRequest()));

        assertThrows(NotFoundException.class, () -> customerService.getCustomerById(anyInt()));

        assertThrows(NotFoundException.class, () -> customerService.updateCustomer(new CustomerRequest(), anyInt()));

        when(customerRepository.findById(anyInt())).thenReturn(Optional.of(new Customer()));
        when(customerRepository.save(any(Customer.class))).thenReturn(new Customer());
        doThrow(InvalidRequestException.class).when(requestValidatorService).validateRequest(new CustomerRequest());
        assertThrows(InvalidRequestException.class, () -> customerService.updateCustomer(
                new CustomerRequest(), 1));
    }

    @Test
    void getCustomerById() throws NotFoundException {
        Customer customer = new Customer();
        customer.setCustomerId(1);
        customer.setName("Kurt");
        customer.setEmail("sample@gmail.com");
        customer.setGender("Male");

        when(customerRepository.findById(anyInt())).thenReturn(Optional.of(customer));
        doNothing().when(kafkaProducerService).publishToTopic(anyString());

        CustomerResponse customerResponse = customerService.getCustomerById(anyInt());

        assertEquals(1, customerResponse.getCustomerId());
        assertEquals("Kurt",customerResponse.getName());
        assertEquals("sample@gmail.com",customerResponse.getEmail());
        assertEquals("Male",customerResponse.getGender());
    }

    @Test
    void getCustomers() {
        Customer customer = new Customer();
        customer.setCustomerId(1);
        customer.setName("Kurt");
        customer.setEmail("sample@gmail.com");
        customer.setGender("Male");

        List<Customer> customers = new ArrayList<>();
        customers.add(customer);

        when(customerRepository.findAll()).thenReturn(customers);
        doNothing().when(kafkaProducerService).publishToTopic(anyString());

        List<CustomerResponse> customerResponses = customerService.getCustomers();

        assertEquals(1, customerResponses.size());
    }

    @Test
    void updateCustomer() throws NotFoundException, InvalidRequestException {

        Customer customer = new Customer();
        customer.setCustomerId(1);
        customer.setName("Kurt");
        customer.setEmail("sample@gmail.com");
        customer.setGender("Male");

        when(customerRepository.save(any(Customer.class))).thenReturn(customer);
        doNothing().when(kafkaProducerService).publishToTopic(anyString());
        doNothing().when(requestValidatorService).validateRequest(any());

        when(customerRepository.findById(anyInt())).thenReturn(Optional.of(customer));

        CustomerResponse customerResponse1 = customerService.getCustomerById(anyInt());

        CustomerRequest customerRequest = new CustomerRequest();
        customerRequest.setName(customerResponse1.getName());
        customerRequest.setEmail(customerResponse1.getEmail());
        customerRequest.setGender(customerResponse1.getGender());
        CustomerResponse customerResponse = customerService.updateCustomer(customerRequest, customer.getCustomerId());

        assertEquals(1, customerResponse.getCustomerId());
        assertEquals("Kurt",customerResponse.getName());
        assertEquals("sample@gmail.com",customerResponse.getEmail());
        assertEquals("Male",customerResponse.getGender());
    }
}