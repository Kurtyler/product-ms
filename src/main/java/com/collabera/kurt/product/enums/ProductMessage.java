package com.collabera.kurt.product.enums;

public enum ProductMessage {
    
    SAVING_PRODUCT("Attempting to save product with request: "),
    SAVED_PRODUCT("Successfully save product with response: "),
    FAILED_SAVING_PRODUCT("Failed to save product with error: "),
    UPDATING_PRODUCT("Attempting to update product with request: "),
    UPDATED_PRODUCT("Successfully updated product with response: "),
    FAILED_UPDATING_PRODUCT("Failed to update product with error: "),
    FETCHING_PRODUCT("Attempting to fetch product/s"),
    FETCHED_PRODUCT("Successfully fetched product/s with response: "),
    FAILED_FETCHING_PRODUCT("Failed to fetch product with error: "),
    PRODUCT_NOT_FOUND("Product not found with Id: ");

    private final String description;

    ProductMessage(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}
