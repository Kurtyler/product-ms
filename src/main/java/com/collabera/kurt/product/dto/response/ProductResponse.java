package com.collabera.kurt.product.dto.response;

import com.collabera.kurt.product.entity.Product;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProductResponse {
    private Integer productId;
    private String productName;
    private Double productPrice;
    private String productDescription;


    public ProductResponse(Product product) {
        this.setProductId(product.getProductId());
        this.setProductName(product.getProductName());
        this.setProductPrice(product.getProductPrice());
        this.setProductDescription(product.getProductDescription());
    }

}
