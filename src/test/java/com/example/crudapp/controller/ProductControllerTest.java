package com.example.crudapp.controller;

import com.example.crudapp.dto.ProductRequestDTO;
import com.example.crudapp.dto.ProductResponseDTO;
import com.example.crudapp.service.ProductService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/** Controller unit tests do not start an HTTP server or perform @Valid validation. */
@ExtendWith(MockitoExtension.class)
class ProductControllerTest {

    @Mock
    private ProductService productService;

    @InjectMocks
    private ProductController controller;

    @Test
    void helloWorld_returnsExpectedText() {
        assertEquals("Hello, World!", controller.helloWorld());
        verifyNoInteractions(productService);
    }

    @Test
    void createProduct_returns201AndDelegatesToService() {
        ProductRequestDTO request = request();
        ProductResponseDTO response = response();
        when(productService.createProduct(request)).thenReturn(response);

        ResponseEntity<ProductResponseDTO> result = controller.createProduct(request);

        assertEquals(HttpStatus.CREATED, result.getStatusCode());
        assertSame(response, result.getBody());
        verify(productService).createProduct(request);
    }

    @Test
    void getProductById_returns200AndDelegatesToService() {
        ProductResponseDTO response = response();
        when(productService.getProductById(10L)).thenReturn(response);

        ResponseEntity<ProductResponseDTO> result = controller.getProductById(10L);

        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertSame(response, result.getBody());
        verify(productService).getProductById(10L);
    }

    @Test
    void getAllProducts_returns200AndProductList() {
        List<ProductResponseDTO> products = List.of(response());
        when(productService.getAllProducts()).thenReturn(products);

        ResponseEntity<List<ProductResponseDTO>> result = controller.getAllProducts();

        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertSame(products, result.getBody());
        verify(productService).getAllProducts();
    }

    @Test
    void updateProduct_returns200AndDelegatesToService() {
        ProductRequestDTO request = request();
        ProductResponseDTO response = response();
        when(productService.updateProduct(10L, request)).thenReturn(response);

        ResponseEntity<ProductResponseDTO> result = controller.updateProduct(10L, request);

        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertSame(response, result.getBody());
        verify(productService).updateProduct(10L, request);
    }

    @Test
    void deleteProduct_returns204AndDelegatesToService() {
        ResponseEntity<Void> result = controller.deleteProduct(10L);

        assertEquals(HttpStatus.NO_CONTENT, result.getStatusCode());
        assertNull(result.getBody());
        verify(productService).deleteProduct(10L);
    }

    private static ProductRequestDTO request() {
        return ProductRequestDTO.builder()
                .name("Mouse")
                .description("Wireless mouse")
                .price(new BigDecimal("49.90"))
                .quantity(5)
                .build();
    }

    private static ProductResponseDTO response() {
        return ProductResponseDTO.builder()
                .id(10L)
                .name("Mouse")
                .description("Wireless mouse")
                .price(new BigDecimal("49.90"))
                .quantity(5)
                .build();
    }
}
