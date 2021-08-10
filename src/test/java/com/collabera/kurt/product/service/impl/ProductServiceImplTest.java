package com.collabera.kurt.product.service.impl;

import com.collabera.kurt.product.dto.request.ProductRequest;
import com.collabera.kurt.product.dto.response.ProductResponse;
import com.collabera.kurt.product.entity.Product;
import com.collabera.kurt.product.exception.InvalidRequestException;
import com.collabera.kurt.product.exception.NotFoundException;
import com.collabera.kurt.product.repository.ProductRepository;
import com.collabera.kurt.product.service.KafkaProducerService;
import com.collabera.kurt.product.service.RequestValidatorService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;


@ExtendWith(SpringExtension.class)
class ProductServiceImplTest {

    @InjectMocks
    ProductServiceImpl productService;

    @Mock
    ProductRepository productRepository;

    @Mock
    KafkaProducerService kafkaProducerService;

    @Mock
    RequestValidatorService requestValidatorService;

    @Test
    void getProducts() {

        List<Product> products = new ArrayList<>();
        products.add(new Product());
        doNothing().when(kafkaProducerService).publishToTopic(anyString());
        when(productRepository.findAll()).thenReturn(products);
        List<ProductResponse> productList = productService.getProducts();

        assertEquals(1, productList.size());

    }

    @Test
    void saveProduct() throws InvalidRequestException {
        when(productRepository.save(new Product())).thenReturn(Product.builder()
                .productId(1)
                .productName("Mango")
                .productPrice(1.00)
                .productDescription("Sample").build());
        doNothing().when(kafkaProducerService).publishToTopic(anyString());
        doNothing().when(requestValidatorService).validateRequest(new ProductRequest());
        ProductResponse productResponse = productService.saveProduct(new ProductRequest());

        assertEquals(1, productResponse.getProductId());
        assertEquals("Mango",productResponse.getProductName());
        assertEquals(1.00, productResponse.getProductPrice());
        assertEquals("Sample", productResponse.getProductDescription());
    }

    @Test
    void exceptionThrows() throws InvalidRequestException {
        assertThrows(InvalidRequestException.class, () -> productService.saveProduct(new ProductRequest()));

        assertThrows(NotFoundException.class, () -> productService.getProductById(anyInt()));

        assertThrows(NotFoundException.class, () -> productService.updateProduct(new ProductRequest(), anyInt()));

        when(productRepository.findById(anyInt())).thenReturn(Optional.of(new Product()));
        when(productRepository.save(any(Product.class))).thenReturn(new Product());
        doThrow(InvalidRequestException.class).when(requestValidatorService).validateRequest(new ProductRequest());
        assertThrows(InvalidRequestException.class, () -> productService.updateProduct(
                new ProductRequest(), 1));
    }

    @Test
    void updateProduct() throws InvalidRequestException, NotFoundException {
        Product product = new Product();
        product.setProductId(1);
        product.setProductName("Mango");
        product.setProductPrice(1.00);
        product.setProductDescription("Sample");

        when(productRepository.save(any(Product.class))).thenReturn(product);
        doNothing().when(kafkaProducerService).publishToTopic(anyString());
        doNothing().when(requestValidatorService).validateRequest(new ProductRequest());

        when(productRepository.findById(anyInt())).thenReturn(java.util.Optional.of(product));

        ProductResponse productResponse1 = productService.getProductById(anyInt());

        ProductRequest productRequest = new ProductRequest();
        productRequest.setProductName(productResponse1.getProductName());
        productRequest.setProductPrice(productResponse1.getProductPrice());
        productRequest.setProductDescription(productResponse1.getProductDescription());
        ProductResponse productResponse = productService.updateProduct(productRequest, product.getProductId());

        assertEquals(1, productResponse.getProductId());
        assertEquals("Mango", productResponse.getProductName());
        assertEquals(1.00, productResponse.getProductPrice());
        assertEquals("Sample", productResponse.getProductDescription());

    }

    @Test
    void getProductById() throws NotFoundException {
        Product product = new Product();
        product.setProductId(1);
        product.setProductName("Mango");
        product.setProductPrice(1.00);
        product.setProductDescription("Sample");

        doNothing().when(kafkaProducerService).publishToTopic(anyString());
        when(productRepository.findById(anyInt())).thenReturn(java.util.Optional.of(product));
        ProductResponse productResponse = productService.getProductById(anyInt());

        assertEquals(1, productResponse.getProductId());
        assertEquals("Mango",productResponse.getProductName());
        assertEquals(1.00, productResponse.getProductPrice());
        assertEquals("Sample", productResponse.getProductDescription());

    }
}