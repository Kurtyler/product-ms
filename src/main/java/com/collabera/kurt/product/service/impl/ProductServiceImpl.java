package com.collabera.kurt.product.service.impl;

import com.collabera.kurt.product.dto.request.ProductRequest;
import com.collabera.kurt.product.dto.response.ProductResponse;
import com.collabera.kurt.product.entity.Product;
import com.collabera.kurt.product.exception.NotFoundException;
import com.collabera.kurt.product.repository.ProductRepository;
import com.collabera.kurt.product.service.KafkaProducerService;
import com.collabera.kurt.product.service.ProductService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Slf4j
@Service
public class ProductServiceImpl implements ProductService {

    private final ProductRepository productRepository;
    private final KafkaProducerService kafkaProducerService;


    @Autowired
    public ProductServiceImpl(final ProductRepository productRepository,
                              final KafkaProducerService kafkaProducerService) {
        this.productRepository = productRepository;
        this.kafkaProducerService = kafkaProducerService;
    }

    @Override
    public List<ProductResponse> getProducts() {
        kafkaProducerService.publishToTopic("Attempting to fetch all products");
        final List<Product> products = productRepository.findAll();
        final List<ProductResponse> productResponseList = new ArrayList<>();
        products.forEach(product -> productResponseList.add(new ProductResponse(product)));
        kafkaProducerService.publishToTopic("Successfully fetched all products: " + productResponseList);
        return productResponseList;
    }

    @Override
    public ProductResponse saveProduct(final ProductRequest productRequest) {
        ProductResponse productResponse = new ProductResponse();
        try {
            kafkaProducerService.publishToTopic(
                    "Attempting to save product with request: " + productRequest);
            productResponse = new ProductResponse(
                    productRepository.save(Product.builder()
                            .productName(productRequest.getProductName())
                            .productPrice(productRequest.getProductPrice())
                            .productDescription(productRequest.getProductDescription())
                            .build()));
            kafkaProducerService.publishToTopic(
                    "Successfully save product with response: " + productResponse);

        } catch (Exception exception) {
            kafkaProducerService.publishToTopic("Failed to save product with error: " + exception.getMessage());
        }
        return productResponse;
    }

    @Override
    public ProductResponse updateProduct(final ProductRequest productRequest, final Integer productId)
            throws NotFoundException {
        ProductResponse productResponse;
        try {
            kafkaProducerService.publishToTopic(
                    "Attempting to update product with request: " + productRequest.toString());
            this.getProductById(productId);
            productResponse = new ProductResponse(
                    productRepository.save(Product.builder()
                            .productId(productId)
                            .productName(productRequest.getProductName())
                            .productPrice(productRequest.getProductPrice())
                            .productDescription(productRequest.getProductDescription())
                            .build()));
            kafkaProducerService.publishToTopic(
                    "Successfully updated product with response: " + productResponse.toString());

        } catch (Exception exception) {
            kafkaProducerService.publishToTopic("Failed to update product with error: " + exception.getMessage());
            throw new NotFoundException(exception.getMessage());
        }
        return productResponse;
    }

    @Override
    public ProductResponse getProductById(final Integer productId) throws NotFoundException {
        final ProductResponse productResponse = new ProductResponse();
        try {
            kafkaProducerService.publishToTopic("Attempting to fetch product with productId: " + productId);
            final Optional<Product> productData = productRepository.findById(productId);
            if (productData.isPresent()) {
                productResponse.setProductId(productData.get().getProductId());
                productResponse.setProductName(productData.get().getProductName());
                productResponse.setProductPrice(productData.get().getProductPrice());
                productResponse.setProductDescription(productData.get().getProductDescription());
                kafkaProducerService.publishToTopic("Successfully fetched product with response: " + productResponse);

            } else {
                kafkaProducerService.publishToTopic(
                        "Failed to fetch product with error: Product not found with Id: " + productId);
                throw new NotFoundException("Product not found with Id: " + productId);
            }

        } catch (final Exception exception) {
            throw new NotFoundException(exception.getMessage());
        }
        return productResponse;
    }


}
