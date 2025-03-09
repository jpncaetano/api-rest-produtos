package com.api.dto;

import com.api.model.Product;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class ProductDTO {
    private long id;
    private String name;
    private String description;
    private BigDecimal price;
    private Integer quantity;
    private String createdBy;

    public ProductDTO(Product product) {
        this.id = product.getId();
        this.name = product.getName();
        this.description = product.getDescription();
        this.price = product.getPrice();
        this.quantity = product.getQuantity();
        this.createdBy = product.getCreatedBy() != null ? product.getCreatedBy().getUsername() : "Desconhecido";
    }
}
