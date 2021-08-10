package com.collabera.kurt.product.service.impl;

import com.collabera.kurt.product.dto.request.ProductRequest;
import com.collabera.kurt.product.dto.response.ProductResponse;
import com.collabera.kurt.product.entity.Product;
import com.collabera.kurt.product.enums.ProductMessage;
import com.collabera.kurt.product.exception.InvalidRequestException;
import com.collabera.kurt.product.exception.NotFoundException;
import com.collabera.kurt.product.repository.ProductRepository;
import com.collabera.kurt.product.service.KafkaProducerService;
import com.collabera.kurt.product.service.ProductService;
import com.collabera.kurt.product.service.RequestValidatorService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class ProductServiceImpl implements ProductService {

    private final ProductRepository productRepository;
    private final KafkaProducerService kafkaProducerService;
    private final RequestValidatorService requestValidatorService;

    /**
     * Product Service to fetch all products
     * @return
     */
    @Override
    public List<ProductResponse> getProducts() {
        kafkaProducerService.publishToTopic(ProductMessage.FETCHING_PRODUCT.getDescription());
        final List<Product> products = productRepository.findAll();
        final List<ProductResponse> productResponseList = new ArrayList<>();
        products.forEach(product -> productResponseList.add(new ProductResponse(product)));
        kafkaProducerService.publishToTopic(ProductMessage.FETCHED_PRODUCT.getDescription() + productResponseList);
        return productResponseList;
    }

    /**
     * Product Service to save product
     * @param productRequest
     * @return
     */
    @Override
    public ProductResponse saveProduct(final ProductRequest productRequest) throws InvalidRequestException {
        final ProductResponse productResponse;
        try {
            kafkaProducerService.publishToTopic(ProductMessage.SAVING_PRODUCT.getDescription() + productRequest);
            requestValidatorService.validateRequest(productRequest);
            productResponse = new ProductResponse(
                    productRepository.save(Product.builder()
                            .productName(productRequest.getProductName())
                            .productPrice(productRequest.getProductPrice())
                            .productDescription(productRequest.getProductDescription())
                            .build()));
            kafkaProducerService.publishToTopic(ProductMessage.SAVED_PRODUCT.getDescription() + productResponse);

        } catch (Exception exception) {
            kafkaProducerService.publishToTopic(
                    ProductMessage.FAILED_SAVING_PRODUCT.getDescription() + exception.getMessage());
            throw new InvalidRequestException(exception.getMessage());
        }
        return productResponse;
    }

    /**
     * Product Service to update product by Product Id
     * @param productRequest
     * @param productId
     * @return
     * @throws NotFoundException
     */
    @Override
    public ProductResponse updateProduct(final ProductRequest productRequest, final Integer productId)
            throws NotFoundException, InvalidRequestException {
        ProductResponse productResponse;

        try {
            kafkaProducerService.publishToTopic(ProductMessage.UPDATING_PRODUCT.getDescription() + productRequest);
            this.getProductById(productId);
            requestValidatorService.validateRequest(productRequest);
            productResponse = new ProductResponse(
                    productRepository.save(Product.builder()
                            .productId(productId)
                            .productName(productRequest.getProductName())
                            .productPrice(productRequest.getProductPrice())
                            .productDescription(productRequest.getProductDescription())
                            .build()));
            kafkaProducerService.publishToTopic(ProductMessage.UPDATED_PRODUCT.getDescription() + productResponse);

        } catch (final NotFoundException notFoundException) {
            kafkaProducerService.publishToTopic(
                    ProductMessage.FAILED_UPDATING_PRODUCT.getDescription() + notFoundException.getMessage());
            throw new NotFoundException(notFoundException.getMessage());

        } catch (InvalidRequestException invalidRequestException) {
            kafkaProducerService.publishToTopic(
                    ProductMessage.FAILED_UPDATING_PRODUCT.getDescription() + invalidRequestException.getMessage());
            throw new InvalidRequestException(invalidRequestException.getMessage());
        }
        return productResponse;
    }

    /**
     * Product Service to fetch product by Product Id
     * @param productId
     * @return
     * @throws NotFoundException
     */
    @Override
    public ProductResponse getProductById(final Integer productId) throws NotFoundException {
        final ProductResponse productResponse = new ProductResponse();
        try {
            kafkaProducerService.publishToTopic(ProductMessage.FETCHING_PRODUCT.getDescription());
            final Optional<Product> productData = productRepository.findById(productId);
            if (!productData.isPresent()) {
                throw new NotFoundException(ProductMessage.PRODUCT_NOT_FOUND.getDescription() + productId);
            }
            productResponse.setProductId(productData.get().getProductId());
            productResponse.setProductName(productData.get().getProductName());
            productResponse.setProductPrice(productData.get().getProductPrice());
            productResponse.setProductDescription(productData.get().getProductDescription());
            kafkaProducerService.publishToTopic(ProductMessage.FETCHED_PRODUCT.getDescription() + productResponse);

        } catch (final Exception exception) {
            kafkaProducerService.publishToTopic(
                    ProductMessage.FAILED_FETCHING_PRODUCT.getDescription() + exception.getMessage());
            throw new NotFoundException(exception.getMessage());
        }
        return productResponse;
    }
}
