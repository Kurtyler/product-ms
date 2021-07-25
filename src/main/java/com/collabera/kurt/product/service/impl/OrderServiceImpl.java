package com.collabera.kurt.product.service.impl;

import com.collabera.kurt.product.dto.request.OrderRequest;
import com.collabera.kurt.product.dto.response.CustomerResponse;
import com.collabera.kurt.product.dto.response.OrderResponse;
import com.collabera.kurt.product.dto.response.ProductResponse;
import com.collabera.kurt.product.entity.Order;
import com.collabera.kurt.product.entity.Product;
import com.collabera.kurt.product.enums.OrderStatusEnum;
import com.collabera.kurt.product.exception.InvalidInputException;
import com.collabera.kurt.product.exception.InvalidOrderException;
import com.collabera.kurt.product.exception.NotFoundException;
import com.collabera.kurt.product.repository.OrderRepository;
import com.collabera.kurt.product.service.CustomerService;
import com.collabera.kurt.product.service.KafkaProducerService;
import com.collabera.kurt.product.service.OrderService;
import com.collabera.kurt.product.service.ProductService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Slf4j
@Service
public class OrderServiceImpl implements OrderService {

    private final OrderRepository orderRepository;

    private final ProductService productService;

    private final CustomerService customerService;

    private final KafkaProducerService kafkaProducerService;

    public OrderServiceImpl(final OrderRepository orderRepository,
                            final ProductService productService,
                            final CustomerService customerService,
                            final KafkaProducerService kafkaProducerService) {
        this.orderRepository = orderRepository;
        this.productService = productService;
        this.customerService = customerService;
        this.kafkaProducerService = kafkaProducerService;
    }

    @Override
    public OrderResponse addOrder(final OrderRequest orderRequest) throws InvalidOrderException {
        final OrderResponse orderResponse;
        try {
            kafkaProducerService.publishToTopic("Attempting to add order with request: " + orderRequest);
            final CustomerResponse customerResponse = customerService.getCustomerById(orderRequest.getCustomerId());
            final ProductResponse product = productService.getProductById(orderRequest.getProductId());
            if (orderRequest.getQuantity() < 1) {
                throw new InvalidInputException("Invalid Quantity: " + orderRequest.getQuantity());
            }
            orderResponse = new OrderResponse(orderRepository.save(Order.builder()
                    .customerId(customerResponse.getCustomerId())
                    .quantity(orderRequest.getQuantity())
                    .products(Product.builder().productId(product.getProductId())
                            .productName(product.getProductName())
                            .productPrice(product.getProductPrice())
                            .productDescription(product.getProductDescription())
                            .build())
                    .status(OrderStatusEnum.PENDING.toString()).build()));
            kafkaProducerService.publishToTopic("Successfully added order with response: " + orderResponse);

        } catch (final Exception exception) {
            kafkaProducerService.publishToTopic("Failed to add order with error: " + exception.getMessage());
            throw new InvalidOrderException(exception.getMessage());
        }
        return orderResponse;
    }

    @Override
    public OrderResponse getOrderById(final Integer orderId) throws NotFoundException {
        final OrderResponse orderResponse = new OrderResponse();
        try {
            kafkaProducerService.publishToTopic("Attempting to fetch order with orderId: " + orderId);
            final Optional<Order> orderData = orderRepository.findById(orderId);
            if (orderData.isPresent()) {
                orderResponse.setOrderId(orderData.get().getOrderId());
                orderResponse.setCustomerId(orderData.get().getCustomerId());
                orderResponse.setProduct(orderData.get().getProducts());
                orderResponse.setQuantity(orderData.get().getQuantity());
                orderResponse.setStatus(orderData.get().getStatus());
                kafkaProducerService.publishToTopic("Successfully fetched order with response: " + orderResponse);

            } else {
                kafkaProducerService.publishToTopic(
                        "Failed to fetch order with error: Order not found with orderId " + orderId);
                throw new NotFoundException("Order not found with orderId: " + orderId);
            }

        } catch (final Exception exception) {
            throw new NotFoundException(exception.getMessage());
        }
        return orderResponse;
    }

    @Override
    public List<OrderResponse> getOrderByCustomerId(final Integer customerId) throws NotFoundException {
        kafkaProducerService.publishToTopic(
                "Attempting to fetch customer order with customerId: " + customerId);
        final List<OrderResponse> orderResponseList = new ArrayList<>();
        try {
            final CustomerResponse customerResponse = customerService.getCustomerById(customerId);
            List<Order> orderData = orderRepository.findOrderByCustomerId(customerResponse.getCustomerId());
            if (!orderData.isEmpty()) {
                orderData.forEach(order -> orderResponseList.add(new OrderResponse(order)));
            } else {
                throw new NotFoundException("Customer order not found for customer with id: " + customerId);
            }
            kafkaProducerService.publishToTopic(
                    "Successfully to fetched customer order with response: " + orderResponseList);

        } catch (final Exception exception) {
            kafkaProducerService.publishToTopic(
                    "Failed to fetch customer order with error: " + exception.getMessage());
            throw new NotFoundException(exception.getMessage());
        }
        return orderResponseList;
    }

    @Override
    public OrderResponse acceptOrderById(final Integer orderId) throws InvalidOrderException {
        final OrderResponse orderResponse;
        try {
            kafkaProducerService.publishToTopic("Attempting to accept order with orderId: " + orderId);
            orderResponse = this.getOrderById(orderId);
            if (orderResponse.getStatus().equals(OrderStatusEnum.PENDING.toString())) {
                orderResponse.setOrderId(orderId);
                orderResponse.setCustomerId(orderResponse.getCustomerId());
                orderResponse.setProduct(orderResponse.getProduct());
                orderResponse.setQuantity(orderResponse.getQuantity());
                orderResponse.setStatus(OrderStatusEnum.ACCEPTED.toString());
                orderRepository.save(Order.builder()
                        .orderId(orderResponse.getOrderId())
                        .customerId(orderResponse.getCustomerId())
                        .products(orderResponse.getProduct())
                        .quantity(orderResponse.getQuantity())
                        .status(OrderStatusEnum.ACCEPTED.toString()).build());

            } else {
                throw new InvalidInputException("Order already ACCEPTED with order id: " + orderId);
            }
            kafkaProducerService.publishToTopic("Successfully accepted order with response: " + orderResponse);

        } catch (final Exception exception) {
            kafkaProducerService.publishToTopic("Failed to accept order with error: " + exception.getMessage());
            throw new InvalidOrderException(exception.getMessage());
        }
        return orderResponse;
    }

    @Override
    public List<OrderResponse> acceptOrderByCustomerId(Integer customerId) throws NotFoundException {
        try {
            kafkaProducerService.publishToTopic(
                    "Attempting to accept customer order/s with customerId: " + customerId);
            final List<OrderResponse> orderResponseList;
            final List<OrderResponse> orderResponses = new ArrayList<>();
            orderResponseList = this.getOrderByCustomerId(customerId);
            orderResponseList.forEach(orderResponse -> {
                if (orderResponse.getStatus().equals(OrderStatusEnum.PENDING.toString())) {
                    orderResponses.add(orderResponse);
                }
            });
            if (orderResponses.isEmpty()) {
                throw new NotFoundException("No order/s to be accepted for customer id: " + customerId);
            }

            for (OrderResponse orderResponse: orderResponses) {
                this.acceptOrderById(orderResponse.getOrderId());
            }
            kafkaProducerService.publishToTopic(
                    "Successfully accepted customer order/s with response: " + this.getOrderByCustomerId(customerId));
        } catch (Exception exception) {
            kafkaProducerService.publishToTopic(
                    "Failed to accept customer order/s with error: " + exception.getMessage());
            throw new NotFoundException(exception.getMessage());
        }
        return this.getOrderByCustomerId(customerId);
    }
}
