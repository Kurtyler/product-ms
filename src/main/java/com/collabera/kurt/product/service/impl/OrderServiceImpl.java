package com.collabera.kurt.product.service.impl;

import com.collabera.kurt.product.dto.request.OrderRequest;
import com.collabera.kurt.product.dto.response.CustomerResponse;
import com.collabera.kurt.product.dto.response.OrderResponse;
import com.collabera.kurt.product.dto.response.ProductResponse;
import com.collabera.kurt.product.entity.Customer;
import com.collabera.kurt.product.entity.Order;
import com.collabera.kurt.product.entity.Product;
import com.collabera.kurt.product.enums.OrderMessage;
import com.collabera.kurt.product.enums.OrderStatus;
import com.collabera.kurt.product.exception.InvalidRequestException;
import com.collabera.kurt.product.exception.InvalidOrderException;
import com.collabera.kurt.product.exception.NotFoundException;
import com.collabera.kurt.product.repository.OrderRepository;
import com.collabera.kurt.product.service.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {

    private final OrderRepository orderRepository;
    private final ProductService productService;
    private final CustomerService customerService;
    private final KafkaProducerService kafkaProducerService;
    private final RequestValidatorService requestValidatorService;

    /**
     * Order Service to add order
     * @param orderRequest
     * @return
     * @throws InvalidOrderException
     */
    @Override
    public OrderResponse addOrder(final OrderRequest orderRequest) throws InvalidOrderException {
        final OrderResponse orderResponse;
        try {
            kafkaProducerService.publishToTopic(OrderMessage.SAVING_ORDER.getDescription() + orderRequest);
            requestValidatorService.validateRequest(orderRequest);
            final CustomerResponse customerResponse = customerService.getCustomerById(orderRequest.getCustomerId());
            final ProductResponse product = productService.getProductById(orderRequest.getProductId());
            if (orderRequest.getQuantity() < 1) {
                throw new InvalidRequestException(
                        OrderMessage.ORDER_INVALID_REQUEST.getDescription() + orderRequest.getQuantity());
            }
            orderResponse = new OrderResponse(orderRepository.save(Order.builder()
                    .customers(Customer.builder().customerId(customerResponse.getCustomerId())
                            .name(customerResponse.getName())
                            .email(customerResponse.getEmail())
                            .gender(customerResponse.getGender())
                            .build())
                    .quantity(orderRequest.getQuantity())
                    .products(Product.builder().productId(product.getProductId())
                            .productName(product.getProductName())
                            .productPrice(product.getProductPrice())
                            .productDescription(product.getProductDescription())
                            .build())
                    .status(OrderStatus.PENDING.toString()).build()));
            kafkaProducerService.publishToTopic(OrderMessage.SAVED_ORDER.getDescription() + orderResponse);

        } catch (final Exception exception) {
            kafkaProducerService.publishToTopic(
                    OrderMessage.FAILED_SAVING_ORDER.getDescription()+ exception.getMessage());
            throw new InvalidOrderException(exception.getMessage());
        }
        return orderResponse;
    }

    /**
     * Order Service to fetch order by Order Id
     * @param orderId
     * @return
     * @throws NotFoundException
     */
    @Override
    public OrderResponse getOrderById(final Integer orderId) throws NotFoundException {
        final OrderResponse orderResponse = new OrderResponse();
        try {
            kafkaProducerService.publishToTopic(OrderMessage.FETCHING_ORDER.getDescription());
            final Optional<Order> orderData = orderRepository.findById(orderId);
            if (!orderData.isPresent()) {
                throw new NotFoundException("Order not found with orderId: " + orderId);
            }

            orderResponse.setOrderId(orderData.get().getOrderId());
            orderResponse.setCustomer(orderData.get().getCustomers());
            orderResponse.setProduct(orderData.get().getProducts());
            orderResponse.setQuantity(orderData.get().getQuantity());
            orderResponse.setStatus(orderData.get().getStatus());
            kafkaProducerService.publishToTopic(OrderMessage.FETCHED_ORDER.getDescription() + orderResponse);

        } catch (final Exception exception) {
            kafkaProducerService.publishToTopic(
                    OrderMessage.FAILED_FETCHING_ORDER.getDescription() + exception.getMessage());
            throw new NotFoundException(exception.getMessage());
        }
        return orderResponse;
    }

    /**
     * Order Service to fetch order/s by Customer Id
     * @param customerId
     * @return
     * @throws NotFoundException
     */
    @Override
    public List<OrderResponse> getOrderByCustomerId(final Integer customerId) throws NotFoundException {
        kafkaProducerService.publishToTopic(OrderMessage.FETCHING_CUSTOMER_ORDER.getDescription() + customerId);
        final List<OrderResponse> orderResponseList = new ArrayList<>();
        try {
            final CustomerResponse customerResponse = customerService.getCustomerById(customerId);
            List<Order> orderData = orderRepository.findOrderByCustomersCustomerId(customerResponse.getCustomerId());
            if (orderData.isEmpty()) {
                throw new NotFoundException(OrderMessage.CUSTOMER_ORDER_NOT_FOUND.getDescription() + customerId);
            }

            orderData.forEach(order -> orderResponseList.add(new OrderResponse(order)));
            kafkaProducerService.publishToTopic(
                    OrderMessage.FETCHED_CUSTOMER_ORDER.getDescription() + orderResponseList);

        } catch (final Exception exception) {
            kafkaProducerService.publishToTopic(
                    OrderMessage.FAILED_FETCHING_CUSTOMER_ORDER.getDescription() + exception.getMessage());
            throw new NotFoundException(exception.getMessage());
        }
        return orderResponseList;
    }

    /**
     * Order Service to accept order by Order Id
     * @param orderId
     * @return
     * @throws InvalidOrderException
     */
    @Override
    public OrderResponse acceptOrderById(final Integer orderId) throws InvalidOrderException {
        final OrderResponse orderResponse;
        try {
            kafkaProducerService.publishToTopic(OrderMessage.ACCEPTING_ORDER.getDescription() + orderId);
            orderResponse = this.getOrderById(orderId);
            if (!orderResponse.getStatus().equals(OrderStatus.PENDING.toString())) {
                throw new InvalidRequestException(OrderMessage.ORDER_ALREADY_ACCEPTED.getDescription() + orderId);
            }

            orderResponse.setOrderId(orderId);
            orderResponse.setCustomer(orderResponse.getCustomer());
            orderResponse.setProduct(orderResponse.getProduct());
            orderResponse.setQuantity(orderResponse.getQuantity());
            orderResponse.setStatus(OrderStatus.ACCEPTED.toString());
            orderRepository.save(Order.builder()
                    .orderId(orderResponse.getOrderId())
                    .customers(orderResponse.getCustomer())
                    .products(orderResponse.getProduct())
                    .quantity(orderResponse.getQuantity())
                    .status(OrderStatus.ACCEPTED.toString()).build());
            kafkaProducerService.publishToTopic(OrderMessage.ACCEPTED_ORDER.getDescription() + orderResponse);

        } catch (final Exception exception) {
            kafkaProducerService.publishToTopic(
                    OrderMessage.FAILED_ACCEPTING_ORDER.getDescription() + exception.getMessage());
            throw new InvalidOrderException(exception.getMessage());
        }
        return orderResponse;
    }

    /**
     * Order Service to accept order by Customer Id
     * @param customerId
     * @return
     * @throws NotFoundException
     */
    @Override
    public List<OrderResponse> acceptOrderByCustomerId(Integer customerId) throws NotFoundException {
        final List<OrderResponse> acceptedOrders = new ArrayList<>();
        try {
            kafkaProducerService.publishToTopic(OrderMessage.ACCEPTING_CUSTOMER_ORDER.getDescription() + customerId);
            final List<OrderResponse> orderResponses = new ArrayList<>();
            final List<OrderResponse> orderResponseList;
            orderResponseList = this.getOrderByCustomerId(customerId);
            orderResponseList.forEach(orderResponse -> {
                if (orderResponse.getStatus().equals(OrderStatus.PENDING.toString())) {
                    orderResponses.add(orderResponse);
                }
            });
            if (orderResponses.isEmpty()) {
                throw new NotFoundException(OrderMessage.NO_PENDING_CUSTOMER_ORDER.getDescription() + customerId);
            }
            for (OrderResponse orderResponse: orderResponses) {
                acceptedOrders.add(this.acceptOrderById(orderResponse.getOrderId()));
            }
            kafkaProducerService.publishToTopic(OrderMessage.ACCEPTED_CUSTOMER_ORDER.getDescription() + orderResponses);
        } catch (Exception exception) {
            kafkaProducerService.publishToTopic(
                    OrderMessage.FAILED_ACCEPTING_CUSTOMER_ORDER.getDescription() + exception.getMessage());
            throw new NotFoundException(exception.getMessage());
        }
        return acceptedOrders;
    }
}
