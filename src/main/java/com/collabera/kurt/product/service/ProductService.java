package com.collabera.kurt.product.service;

import com.collabera.kurt.product.dto.request.ProductRequest;
import com.collabera.kurt.product.dto.response.ProductResponse;
import com.collabera.kurt.product.exception.NotFoundException;

import java.util.List;

public interface ProductService {

    List<ProductResponse> getProducts();

    ProductResponse saveProduct(ProductRequest productRequest);

    ProductResponse updateProduct(ProductRequest productRequest, Integer productId) throws NotFoundException;

    ProductResponse getProductById(Integer productId) throws NotFoundException;
}
