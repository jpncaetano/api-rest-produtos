
package com.api.controller;

import com.api.dto.ProductDTO;
import com.api.model.Product;
import com.api.service.ProductService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@Tag(name = "Produtos", description = "Endpoints para gerenciamento de produtos")
@RestController
@RequestMapping("/products")
public class ProductController {
    private final ProductService productService;

    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    // Endpoints Públicos (Acesso Livre)

    @Operation(summary = "Lista todos os produtos com paginação e ordenação",
            description = "Retorna produtos paginados, ordenados por nome ou preço")
    @ApiResponse(responseCode = "200", description = "Produtos retornados com sucesso")
    @GetMapping
    public ResponseEntity<Page<ProductDTO>> getAllProducts(
            @Parameter(description = "Número da página (0 = primeira)") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Quantidade de itens por página") @RequestParam(defaultValue = "10") int size,
            @Parameter(description = "Ordenação no formato: campo,direção (ex: name,asc)") @RequestParam(defaultValue = "name,asc") String[] sort
    ) {
        return ResponseEntity.ok(productService.getAllProducts(page, size, sort));
    }

    @Operation(summary = "Busca produto por ID")
    @ApiResponse(responseCode = "200", description = "Produto encontrado com sucesso")
    @GetMapping("/{id}")
    public ResponseEntity<ProductDTO> getProductById(@PathVariable Long id) {
        return ResponseEntity.ok(productService.getProductById(id));
    }

    @Operation(summary = "Lista produtos ordenados por preço (ASC/DESC)")
    @ApiResponse(responseCode = "200", description = "Lista de produtos ordenada por preço")
    @GetMapping(params = "sort")
    public ResponseEntity<List<ProductDTO>> getProductsSorted(@RequestParam String sort) {
        return ResponseEntity.ok(productService.getProductsSorted(sort));
    }

    @Operation(summary = "Lista produtos cadastrados por um usuário específico")
    @ApiResponse(responseCode = "200", description = "Produtos do usuário retornados com sucesso")
    @GetMapping(params = "userId")
    public ResponseEntity<List<ProductDTO>> getProductsByUser(@RequestParam Long userId) {
        return ResponseEntity.ok(productService.getProductsByUser(userId));
    }

    // Endpoints Protegidos (Apenas SELLERs e ADMINs)

    @Operation(summary = "Lista produtos do usuário autenticado (SELLER ou ADMIN)")
    @ApiResponse(responseCode = "200", description = "Produtos do usuário autenticado retornados")
    @GetMapping("/mine")
    @PreAuthorize("hasAnyRole('SELLER', 'ADMIN')")
    public ResponseEntity<List<ProductDTO>> getMyProducts(Principal principal) {
        return ResponseEntity.ok(productService.getMyProducts(principal.getName()));
    }

    @Operation(summary = "Cria um novo produto (SELLER ou ADMIN)")
    @ApiResponse(responseCode = "200", description = "Produto criado com sucesso")
    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'SELLER')")
    public ResponseEntity<ProductDTO> createProduct(@RequestBody ProductDTO productDTO, Principal principal) {
        return ResponseEntity.ok(productService.createProduct(productDTO, principal.getName()));
    }

    @Operation(summary = "Atualiza um produto existente")
    @ApiResponse(responseCode = "200", description = "Produto atualizado com sucesso")
    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('SELLER', 'ADMIN')")
    public ResponseEntity<ProductDTO> updateProduct(@PathVariable Long id, @RequestBody ProductDTO productDTO, Principal principal) {
        return ResponseEntity.ok(productService.updateProduct(id, productDTO, principal.getName()));
    }

    @Operation(summary = "Atualiza o estoque de um produto")
    @ApiResponse(responseCode = "200", description = "Estoque do produto atualizado")
    @PatchMapping("/{id}/stock")
    @PreAuthorize("hasAnyRole('SELLER', 'ADMIN')")
    public ResponseEntity<ProductDTO> updateProductStock(@PathVariable Long id, @RequestParam int quantity, Principal principal) {
        return ResponseEntity.ok(productService.updateProductStock(id, quantity, principal.getName()));
    }

    @Operation(summary = "Remove um produto do sistema")
    @ApiResponse(responseCode = "204", description = "Produto removido com sucesso")
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'SELLER')")
    public ResponseEntity<Void> deleteProduct(@PathVariable Long id, Principal principal) {
        productService.deleteProduct(id, principal.getName());
        return ResponseEntity.noContent().build();
    }
}