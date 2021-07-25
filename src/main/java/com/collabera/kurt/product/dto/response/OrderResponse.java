package com.collabera.kurt.product.dto.response;

import com.collabera.kurt.product.entity.Order;
import com.collabera.kurt.product.entity.Product;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrderResponse {

    private Integer orderId;
    private Integer customerId;
    private Product product;
    private Integer quantity;
    private String status;

    public OrderResponse(Order order) {
        this.setOrderId(order.getOrderId());
        this.setCustomerId(order.getCustomerId());
        this.setProduct(order.getProducts());
        this.setQuantity(order.getQuantity());
        this.setStatus(order.getStatus());
    }
}
