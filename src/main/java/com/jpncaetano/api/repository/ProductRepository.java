package com.jpncaetano.api.repository;

import com.jpncaetano.api.model.Product;
import com.jpncaetano.api.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {

    // Retorna todos os produtos criados por um usuário específico
    List<Product> findByCreatedBy(User createdBy);

    // Retorna todos os produtos ordenados por preço (ASC ou DESC)
    @Query("SELECT p FROM Product p ORDER BY p.price " +
            "ASC NULLS LAST") // NULLS LAST para evitar problemas com preços nulos
    List<Product> findAllSortedByPriceAsc();

    @Query("SELECT p FROM Product p ORDER BY p.price " +
            "DESC NULLS LAST")
    List<Product> findAllSortedByPriceDesc();

    // Busca produtos ordenados dinamicamente pelo parâmetro recebido
    default List<Product> findAllSortedByPrice(String sort) {
        return sort.equalsIgnoreCase("asc") ? findAllSortedByPriceAsc() : findAllSortedByPriceDesc();
    }
}
