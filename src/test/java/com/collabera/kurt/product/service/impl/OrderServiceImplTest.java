package com.collabera.kurt.product.service.impl;

import com.collabera.kurt.product.dto.request.OrderRequest;
import com.collabera.kurt.product.dto.response.CustomerResponse;
import com.collabera.kurt.product.dto.response.OrderResponse;
import com.collabera.kurt.product.dto.response.ProductResponse;
import com.collabera.kurt.product.entity.Customer;
import com.collabera.kurt.product.entity.Order;
import com.collabera.kurt.product.entity.Product;
import com.collabera.kurt.product.enums.OrderStatus;
import com.collabera.kurt.product.exception.InvalidRequestException;
import com.collabera.kurt.product.exception.InvalidOrderException;
import com.collabera.kurt.product.exception.NotFoundException;
import com.collabera.kurt.product.repository.CustomerRepository;
import com.collabera.kurt.product.repository.OrderRepository;
import com.collabera.kurt.product.service.*;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OrderServiceImplTest {

    @InjectMocks
    OrderServiceImpl orderService;

    @Mock
    KafkaProducerService kafkaProducerService;

    @Mock
    OrderRepository orderRepository;

    @Mock
    CustomerServiceImpl customerService;

    @Mock
    CustomerRepository customerRepository;

    @Mock
    RequestValidatorService requestValidatorService;

    @Mock
    ProductService productService;

    @Test
    void addOrder() throws InvalidRequestException, NotFoundException, InvalidOrderException {

        doNothing().when(kafkaProducerService).publishToTopic(anyString());

        doNothing().when(requestValidatorService).validateRequest(any());

        when(customerService.getCustomerById(anyInt())).thenReturn(new CustomerResponse());

        when(productService.getProductById(anyInt())).thenReturn(new ProductResponse());

        when(orderRepository.save(any(Order.class))).thenReturn(Order.builder()
                .orderId(1)
                .customers(new Customer())
                .products(new Product())
                .quantity(1)
                .status(OrderStatus.PENDING.toString())
                .build());

        OrderRequest orderRequest = new OrderRequest();
        orderRequest.setCustomerId(1);
        orderRequest.setProductId(1);
        orderRequest.setQuantity(1);

        OrderResponse orderResponse = orderService.addOrder(orderRequest);

        assertEquals(1, orderResponse.getOrderId());
    }

    @Test
    void addOrderThrowsInvalidInput() throws NotFoundException, InvalidRequestException, InvalidOrderException {
        doNothing().when(kafkaProducerService).publishToTopic(anyString());

        doNothing().when(requestValidatorService).validateRequest(any());

        when(customerService.getCustomerById(anyInt())).thenReturn(new CustomerResponse());

        when(productService.getProductById(anyInt())).thenReturn(new ProductResponse());

        OrderRequest orderRequest = new OrderRequest();
        orderRequest.setCustomerId(1);
        orderRequest.setProductId(1);
        orderRequest.setQuantity(0);

        assertThrows(InvalidOrderException.class, () -> orderService.addOrder(orderRequest));
    }

    @Test
    void getOrderById() throws NotFoundException {

        Order order = new Order();
        order.setOrderId(1);
        order.setCustomers(new Customer());
        order.setProducts(new Product());
        order.setQuantity(1);

        doNothing().when(kafkaProducerService).publishToTopic(anyString());
        when(orderRepository.findById(anyInt())).thenReturn(Optional.of(order));
        OrderResponse orderResponse = orderService.getOrderById(anyInt());

        assertEquals(1, orderResponse.getOrderId());
        assertEquals(1, orderResponse.getQuantity());
    }

    @Test
    void exceptionThrow() {
        assertThrows(NotFoundException.class, () -> orderService.getOrderById(anyInt()));

        assertThrows(NotFoundException.class, () -> orderService.getOrderByCustomerId(anyInt()));
    }

    @Test
    void getOrderByCustomerId() throws NotFoundException {

        List<Order> orders = new ArrayList<>();
        orders.add(new Order());

        doNothing().when(kafkaProducerService).publishToTopic(anyString());

        when(customerService.getCustomerById(anyInt())).thenReturn(new CustomerResponse());

        when(orderRepository.findOrderByCustomersCustomerId(any())).thenReturn(orders);

        List<OrderResponse> orderResponses = orderService.getOrderByCustomerId(1);

        assertEquals(1, orderResponses.size());
    }

    @Test
    void getOrderCustomerIdNotFound() throws NotFoundException {

        List<Order> orders = new ArrayList<>();

        doNothing().when(kafkaProducerService).publishToTopic(anyString());

        when(customerRepository.findById(anyInt())).thenReturn(Optional.of(new Customer()));

        when(customerService.getCustomerById(anyInt())).thenReturn(new CustomerResponse());

        when(orderRepository.findOrderByCustomersCustomerId(2)).thenReturn(orders);

        assertThrows(NotFoundException.class, () -> orderService.getOrderByCustomerId(1));
    }

    @Test
    void acceptOrderById() throws InvalidOrderException, NotFoundException {

        Order order = new Order();
        order.setOrderId(1);
        order.setCustomers(new Customer());
        order.setProducts(new Product());
        order.setQuantity(1);
        order.setStatus(OrderStatus.PENDING.toString());

        doNothing().when(kafkaProducerService).publishToTopic(anyString());

        when(orderRepository.findById(anyInt())).thenReturn(Optional.of(order));

        orderService.getOrderById(anyInt());

        when(orderRepository.save(any(Order.class))).thenReturn(Order.builder()
                .orderId(1)
                .customers(new Customer())
                .products(new Product())
                .quantity(1)
                .status(OrderStatus.ACCEPTED.toString())
                .build());
        OrderResponse orderResponse = orderService.acceptOrderById(1);

        assertEquals(OrderStatus.ACCEPTED.toString(), orderResponse.getStatus());

    }

    @Test
    void acceptOrderByIdThrowsInvalidOrderException() throws NotFoundException {

        Order order = new Order();
        order.setOrderId(1);
        order.setCustomers(new Customer());
        order.setProducts(new Product());
        order.setQuantity(1);
        order.setStatus(OrderStatus.ACCEPTED.toString());

        doNothing().when(kafkaProducerService).publishToTopic(anyString());

        when(orderRepository.findById(anyInt())).thenReturn(Optional.of(order));

        orderService.getOrderById(anyInt());

        assertThrows(InvalidOrderException.class, () -> orderService.acceptOrderById(1));
    }

    @Test
    void acceptOrderByCustomerId() throws NotFoundException, InvalidOrderException {
        Order order = new Order();
        order.setOrderId(1);
        order.setCustomers(new Customer());
        order.setProducts(new Product());
        order.setQuantity(1);
        order.setStatus(OrderStatus.PENDING.toString());

        List<Order> orders = new ArrayList<>();
        orders.add(order);

        doNothing().when(kafkaProducerService).publishToTopic(anyString());

        when(customerService.getCustomerById(anyInt())).thenReturn(new CustomerResponse());

        when(orderRepository.findOrderByCustomersCustomerId(any())).thenReturn(orders);

        orderService.getOrderByCustomerId(anyInt());

        when(orderRepository.findById(anyInt())).thenReturn(Optional.of(order));

        orderService.getOrderById(anyInt());

        when(orderRepository.save(any(Order.class))).thenReturn(Order.builder()
                .orderId(1)
                .customers(new Customer())
                .products(new Product())
                .quantity(1)
                .status(OrderStatus.ACCEPTED.toString())
                .build());

        orderService.acceptOrderById(anyInt());

        List<OrderResponse> orderResponses = orderService.acceptOrderByCustomerId(1);

        assertEquals(1, orderResponses.size());

    }

    @Test
    void acceptOrderByCustomerIdThrowsNotFoundException() throws NotFoundException, InvalidOrderException {
        Order order = new Order();
        order.setOrderId(1);
        order.setCustomers(new Customer());
        order.setProducts(new Product());
        order.setQuantity(1);
        order.setStatus(OrderStatus.ACCEPTED.toString());

        List<Order> orders = new ArrayList<>();
        orders.add(order);

        doNothing().when(kafkaProducerService).publishToTopic(anyString());

        when(customerService.getCustomerById(anyInt())).thenReturn(new CustomerResponse());

        when(orderRepository.findOrderByCustomersCustomerId(any())).thenReturn(orders);

        assertThrows(NotFoundException.class, () -> orderService.acceptOrderByCustomerId(1));
    }
}