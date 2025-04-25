package com.api.dto;

import com.api.model.Product;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class ProductDTO {

    @Schema(description = "ID do produto", example = "1")
    private long id;

    @Schema(description = "Nome do produto", example = "Camiseta Oversized")
    private String name;

    @Schema(description = "Descrição detalhada do produto", example = "Camiseta de algodão premium")
    private String description;

    @Schema(description = "Preço do produto", example = "99.90")
    private BigDecimal price;

    @Schema(description = "Quantidade em estoque", example = "15")
    private Integer quantity;

    @Schema(description = "Nome de quem criou o produto", example = "joao")
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
