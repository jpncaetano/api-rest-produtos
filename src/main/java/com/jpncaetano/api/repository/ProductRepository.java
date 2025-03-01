package com.jpncaetano.api.repository;

import com.jpncaetano.api.model.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {
    // Métodos adicionais podem ser adicionados aqui, se necessário
}
