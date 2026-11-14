package com.example.crudapp.service;

import com.example.crudapp.dto.ProductRequestDTO;
import com.example.crudapp.dto.ProductResponseDTO;
import com.example.crudapp.entity.Product;
import com.example.crudapp.exception.ResourceNotFoundException;
import com.example.crudapp.repository.ProductRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * Unit tests: ProductRepository is a Mockito mock; no Spring context or Oracle database.
 */
@ExtendWith(MockitoExtension.class)
class ProductServiceImplTest {

    private static final LocalDateTime CREATED_AT = LocalDateTime.of(2026, 11, 1, 10, 0);
    private static final LocalDateTime UPDATED_AT = LocalDateTime.of(2026, 11, 2, 12, 30);

    @Mock
    private ProductRepository productRepository;

    @InjectMocks
    private ProductServiceImpl productService;

    @Test
    void createProduct_savesEntityAndReturnsResponse() {
        ProductRequestDTO request = request("Keyboard", "Mechanical", "199.90", 12);

        // A mocked save does not generate an ID or call JPA @PrePersist callbacks.
        // Simulate the persisted entity being returned by the repository.
        when(productRepository.save(any(Product.class))).thenAnswer(invocation -> {
            Product saved = invocation.getArgument(0);
            saved.setId(101L);
            saved.setCreatedAt(CREATED_AT);
            saved.setUpdatedAt(UPDATED_AT);
            return saved;
        });

        ProductResponseDTO result = productService.createProduct(request);

        ArgumentCaptor<Product> captor = ArgumentCaptor.forClass(Product.class);
        verify(productRepository).save(captor.capture());
        Product saved = captor.getValue();
        assertAll(
                () -> assertEquals("Keyboard", saved.getName()),
                () -> assertEquals("Mechanical", saved.getDescription()),
                () -> assertEquals(new BigDecimal("199.90"), saved.getPrice()),
                () -> assertEquals(12, saved.getQuantity()),
                () -> assertEquals(101L, result.getId()),
                () -> assertEquals(saved.getName(), result.getName()),
                () -> assertEquals(saved.getDescription(), result.getDescription()),
                () -> assertEquals(saved.getPrice(), result.getPrice()),
                () -> assertEquals(saved.getQuantity(), result.getQuantity()),
                () -> assertEquals(CREATED_AT, result.getCreatedAt()),
                () -> assertEquals(UPDATED_AT, result.getUpdatedAt())
        );
    }

    @Test
    void createProduct_propagatesRepositoryFailure() {
        when(productRepository.save(any(Product.class)))
                .thenThrow(new IllegalStateException("Database unavailable"));

        IllegalStateException exception = assertThrows(IllegalStateException.class,
                () -> productService.createProduct(request("Keyboard", "Mechanical", "199.90", 12)));

        assertEquals("Database unavailable", exception.getMessage());
        verify(productRepository).save(any(Product.class));
    }

    @Test
    void getProductById_returnsMappedResponseWhenFound() {
        Product entity = product(15L, "Mouse", "Wireless", "49.90", 5);
        when(productRepository.findById(15L)).thenReturn(Optional.of(entity));

        ProductResponseDTO result = productService.getProductById(15L);

        assertAll(
                () -> assertEquals(15L, result.getId()),
                () -> assertEquals("Mouse", result.getName()),
                () -> assertEquals("Wireless", result.getDescription()),
                () -> assertEquals(new BigDecimal("49.90"), result.getPrice()),
                () -> assertEquals(5, result.getQuantity()),
                () -> assertEquals(CREATED_AT, result.getCreatedAt()),
                () -> assertEquals(UPDATED_AT, result.getUpdatedAt())
        );
        verify(productRepository).findById(15L);
    }

    @Test
    void getProductById_throwsWhenProductDoesNotExist() {
        when(productRepository.findById(404L)).thenReturn(Optional.empty());

        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class,
                () -> productService.getProductById(404L));

        assertEquals("Product not found with id: 404", exception.getMessage());
        verify(productRepository).findById(404L);
    }

    @Test
    void getAllProducts_mapsEveryEntityToResponseDTO() {
        when(productRepository.findAll()).thenReturn(List.of(
                product(1L, "Monitor", "27 inch", "699.00", 3),
                product(2L, "Mouse", "Wireless", "49.90", 20)
        ));

        List<ProductResponseDTO> result = productService.getAllProducts();

        assertEquals(2, result.size());
        assertAll(
                () -> assertEquals(1L, result.get(0).getId()),
                () -> assertEquals("Monitor", result.get(0).getName()),
                () -> assertEquals(new BigDecimal("699.00"), result.get(0).getPrice()),
                () -> assertEquals(2L, result.get(1).getId()),
                () -> assertEquals("Mouse", result.get(1).getName()),
                () -> assertEquals(20, result.get(1).getQuantity())
        );
        verify(productRepository).findAll();
    }

    @Test
    void getAllProducts_returnsEmptyListWhenRepositoryIsEmpty() {
        when(productRepository.findAll()).thenReturn(List.of());

        List<ProductResponseDTO> result = productService.getAllProducts();

        assertNotNull(result);
        assertTrue(result.isEmpty());
        verify(productRepository).findAll();
    }

    @Test
    void updateProduct_modifiesExistingEntityAndReturnsResponse() {
        Product existing = product(8L, "Old name", "Old description", "9.99", 1);
        ProductRequestDTO request = request("New name", "New description", "109.50", 6);
        when(productRepository.findById(8L)).thenReturn(Optional.of(existing));
        when(productRepository.save(same(existing))).thenReturn(existing);

        ProductResponseDTO result = productService.updateProduct(8L, request);

        // Check both that the correct existing entity was saved and that fields were updated.
        verify(productRepository).findById(8L);
        verify(productRepository).save(same(existing));
        assertAll(
                () -> assertEquals(8L, existing.getId()),
                () -> assertEquals("New name", existing.getName()),
                () -> assertEquals("New description", existing.getDescription()),
                () -> assertEquals(new BigDecimal("109.50"), existing.getPrice()),
                () -> assertEquals(6, existing.getQuantity()),
                () -> assertEquals(8L, result.getId()),
                () -> assertEquals(existing.getName(), result.getName()),
                () -> assertEquals(existing.getDescription(), result.getDescription()),
                () -> assertEquals(existing.getPrice(), result.getPrice()),
                () -> assertEquals(existing.getQuantity(), result.getQuantity())
        );
    }

    @Test
    void updateProduct_throwsWhenProductDoesNotExistAndNeverSaves() {
        when(productRepository.findById(404L)).thenReturn(Optional.empty());

        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class,
                () -> productService.updateProduct(404L, request("Missing", null, "3.00", 1)));

        assertEquals("Product not found with id: 404", exception.getMessage());
        verify(productRepository).findById(404L);
        verify(productRepository, never()).save(any(Product.class));
    }

    @Test
    void deleteProduct_deletesWhenProductExists() {
        when(productRepository.existsById(7L)).thenReturn(true);

        productService.deleteProduct(7L);

        verify(productRepository).existsById(7L);
        verify(productRepository).deleteById(7L);
    }

    @Test
    void deleteProduct_throwsWhenProductDoesNotExistAndNeverDeletes() {
        when(productRepository.existsById(404L)).thenReturn(false);

        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class,
                () -> productService.deleteProduct(404L));

        assertEquals("Product not found with id: 404", exception.getMessage());
        verify(productRepository).existsById(404L);
        verify(productRepository, never()).deleteById(any(Long.class));
    }

    @Test
    void deleteProduct_propagatesDeleteFailure() {
        when(productRepository.existsById(7L)).thenReturn(true);
        doThrow(new IllegalStateException("Delete failed"))
                .when(productRepository).deleteById(7L);

        IllegalStateException exception = assertThrows(IllegalStateException.class,
                () -> productService.deleteProduct(7L));

        assertEquals("Delete failed", exception.getMessage());
        verify(productRepository).deleteById(7L);
    }

    private static ProductRequestDTO request(String name, String description,
                                             String price, int quantity) {
        return ProductRequestDTO.builder()
                .name(name)
                .description(description)
                .price(new BigDecimal(price))
                .quantity(quantity)
                .build();
    }

    private static Product product(Long id, String name, String description,
                                   String price, int quantity) {
        return Product.builder()
                .id(id)
                .name(name)
                .description(description)
                .price(new BigDecimal(price))
                .quantity(quantity)
                .createdAt(CREATED_AT)
                .updatedAt(UPDATED_AT)
                .build();
    }
}
