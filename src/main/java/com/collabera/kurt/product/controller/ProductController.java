package com.collabera.kurt.product.controller;

import com.collabera.kurt.product.dto.request.ProductRequest;
import com.collabera.kurt.product.dto.response.ProductResponse;
import com.collabera.kurt.product.exception.NotFoundException;
import com.collabera.kurt.product.service.ProductService;
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

    @PostMapping("/saveProduct")
    public ResponseEntity<ProductResponse> saveProduct(@RequestBody final ProductRequest productRequest) {
        return ResponseEntity.status(HttpStatus.OK).body(productService.saveProduct(productRequest));
    }

    @GetMapping("/getProducts")
    public ResponseEntity<List<ProductResponse>> getProducts() {
        List<ProductResponse> productResponseList = productService.getProducts();
        return ResponseEntity.status(HttpStatus.OK).body(productResponseList);
    }

    @PutMapping("/updateProduct/{productId}")
    public ResponseEntity<ProductResponse> updateProduct(@RequestBody final ProductRequest productRequest,
                                           @PathVariable("productId") Integer productId) throws NotFoundException {
        return ResponseEntity.status(HttpStatus.OK).body(productService.updateProduct(productRequest, productId));
    }

    @GetMapping("/getProductById/{productId}")
    public ResponseEntity<ProductResponse> getProductById(@PathVariable("productId") final Integer productId)
            throws NotFoundException {
        return ResponseEntity.status(HttpStatus.OK).body(productService.getProductById(productId));
    }

}
