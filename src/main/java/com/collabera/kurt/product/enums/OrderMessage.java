package com.collabera.kurt.product.enums;

public enum OrderMessage {
    SAVING_ORDER("Attempting to add order with request: "),
    SAVED_ORDER("Successfully added order with response: "),
    FAILED_SAVING_ORDER("Failed to add order with error: " ),
    FETCHING_ORDER("Attempting to fetch order/s"),
    FETCHED_ORDER("Successfully fetched order with response: " ),
    FAILED_FETCHING_ORDER("Failed to fetch order with error: "),
    FETCHING_CUSTOMER_ORDER("Attempting to fetch customer order with customerId: "),
    FETCHED_CUSTOMER_ORDER("Successfully to fetched customer order with response: "),
    FAILED_FETCHING_CUSTOMER_ORDER("Failed to fetch customer order with error: "),
    ACCEPTING_ORDER("Attempting to accept order with orderId: "),
    ACCEPTED_ORDER("Successfully accepted order with response: "),
    FAILED_ACCEPTING_ORDER("Failed to accept order with error: "),
    ACCEPTING_CUSTOMER_ORDER("Attempting to accept customer order/s with customerId: "),
    ACCEPTED_CUSTOMER_ORDER("Successfully accepted customer order/s with response: "),
    FAILED_ACCEPTING_CUSTOMER_ORDER("Failed to accept customer order/s with error: "),
    CUSTOMER_ORDER_NOT_FOUND("Customer order not found for customer with id: "),
    NO_PENDING_CUSTOMER_ORDER("No order/s to be accepted for customer id: "),
    ORDER_ALREADY_ACCEPTED("Order already ACCEPTED with order id: "),
    ORDER_INVALID_REQUEST("Value for field quantity cannot be: ");

    private final String description;

    OrderMessage(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}
