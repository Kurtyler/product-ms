package com.collabera.kurt.product.enums;

public enum CustomerMessage {

    SAVING_CUSTOMER("Attempting to add customer with request: "),
    SAVED_CUSTOMER("Successfully added customer with response: "),
    FAILED_SAVING_CUSTOMER("Failed to save customer with error: "),
    FETCHING_CUSTOMER("Attempting to fetch customer/s"),
    FETCHED_CUSTOMER("Successfully fetched customer/s with response: "),
    FAILED_FETCHING_CUSTOMER("Failed to fetch customer/s with error: "),
    UPDATING_CUSTOMER("Attempting to update product with request: "),
    UPDATED_CUSTOMER("Successfully updated customer with response: "),
    FAILED_UPDATING_CUSTOMER("Failed to update customer with error: "),
    CUSTOMER_NOT_FOUND("Customer not found with Id: ");

    private final String description;

    CustomerMessage(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}
