package com.commerce.product.service;

import com.commerce.product.dto.ProductRequestDTO;
import com.commerce.product.dto.ProductResponseDTO;

import java.util.List;

public interface ProductService {

    ProductResponseDTO createProduct(ProductRequestDTO request);

    ProductResponseDTO getProductById(Long id);

    List<ProductResponseDTO> getAllProducts();

    ProductResponseDTO updateProduct(Long id, ProductRequestDTO request);

    void deleteProduct(Long id);

}
