package com.example.crudapp.repository;

import com.example.crudapp.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {
    // CRUD + paging/sorting come from JpaRepository.
    // Add derived-query methods here as needed, e.g.:
    // List<Product> findByNameContainingIgnoreCase(String name);
}
