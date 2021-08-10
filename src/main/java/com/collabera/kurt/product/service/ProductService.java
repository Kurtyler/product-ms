package com.collabera.kurt.product.service;

import com.collabera.kurt.product.dto.request.ProductRequest;
import com.collabera.kurt.product.dto.response.ProductResponse;
import com.collabera.kurt.product.exception.InvalidRequestException;
import com.collabera.kurt.product.exception.NotFoundException;

import java.util.List;

public interface ProductService {

    List<ProductResponse> getProducts();

    ProductResponse saveProduct(ProductRequest productRequest) throws InvalidRequestException;

    ProductResponse updateProduct(ProductRequest productRequest, Integer productId)
            throws NotFoundException, InvalidRequestException;

    ProductResponse getProductById(Integer productId) throws NotFoundException;
}
