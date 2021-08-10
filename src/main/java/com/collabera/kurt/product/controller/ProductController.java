package com.collabera.kurt.product.controller;

import com.collabera.kurt.product.dto.request.ProductRequest;
import com.collabera.kurt.product.dto.response.ProductResponse;
import com.collabera.kurt.product.exception.InvalidRequestException;
import com.collabera.kurt.product.exception.NotFoundException;
import com.collabera.kurt.product.service.ProductService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/product")
public class ProductController {
    private final ProductService productService;

    @Autowired
    public ProductController(final ProductService productService) {
        this.productService = productService;
    }

    @Operation(summary = "This is to add product")
    @PostMapping("/addProduct")
    public ResponseEntity<ProductResponse> saveProduct(@RequestBody final ProductRequest productRequest)
            throws InvalidRequestException {
        return ResponseEntity.status(HttpStatus.OK).body(productService.saveProduct(productRequest));
    }

    @Operation(summary = "This is to fetch all products")
    @GetMapping("/getProducts")
    public ResponseEntity<List<ProductResponse>> getProducts() {
        List<ProductResponse> productResponseList = productService.getProducts();
        return ResponseEntity.status(HttpStatus.OK).body(productResponseList);
    }

    @Operation(summary = "This is to update product by Product Id")
    @PutMapping("/updateProduct/{productId}")
    public ResponseEntity<ProductResponse> updateProduct(@RequestBody final ProductRequest productRequest,
                                           @PathVariable("productId") Integer productId)
            throws NotFoundException, InvalidRequestException {
        return ResponseEntity.status(HttpStatus.OK).body(productService.updateProduct(productRequest, productId));
    }

    @Operation(summary = "This is to fetch product by Product Id")
    @GetMapping("/getProductById/{productId}")
    public ResponseEntity<ProductResponse> getProductById(@PathVariable("productId") final Integer productId)
            throws NotFoundException {
        return ResponseEntity.status(HttpStatus.OK).body(productService.getProductById(productId));
    }

}
