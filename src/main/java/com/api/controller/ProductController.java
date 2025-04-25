package com.api.controller;

import com.api.dto.ProductDTO;
import com.api.model.Product;
import com.api.service.ProductService;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("/products")
public class ProductController {
    private final ProductService productService;

    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    // Endpoints Públicos (Acesso Livre)
    /**
     * Retorna a lista de todos os produtos cadastrados.
     */
    @GetMapping
    public ResponseEntity<Page<ProductDTO>> getAllProducts(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "name,asc") String[] sort
    ) {
        return ResponseEntity.ok(productService.getAllProducts(page, size, sort));
    }

    /**
     * Retorna um produto pelo ID, se encontrado.
     */
    @GetMapping("/{id}")
    public ResponseEntity<ProductDTO> getProductById(@PathVariable Long id) {
        return ResponseEntity.ok(productService.getProductById(id));
    }

    /**
     * Retorna a lista de produtos ordenados por preço (ASC/DESC).
     */
    @GetMapping(params = "sort")
    public ResponseEntity<List<ProductDTO>> getProductsSorted(@RequestParam String sort) {
        return ResponseEntity.ok(productService.getProductsSorted(sort));
    }

    /**
     * Retorna a lista de produtos cadastrados por um usuário (SELLER ou ADMIN).
     */
    @GetMapping(params = "userId")
    public ResponseEntity<List<ProductDTO>> getProductsByUser(@RequestParam Long userId) {
        return ResponseEntity.ok(productService.getProductsByUser(userId));
    }


    // Endpoints Protegidos (Apenas SELLERs e ADMINs)
    /**
     * Retorna os produtos cadastrados pelo próprio usuário autenticado.
     */
    @GetMapping("/mine")
    @PreAuthorize("hasAnyRole('SELLER', 'ADMIN')")
    public ResponseEntity<List<ProductDTO>> getMyProducts(Principal principal) {
        return ResponseEntity.ok(productService.getMyProducts(principal.getName()));
    }

    /**
     * Permite a criação de um novo produto.
     */
    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'SELLER')")
    public ResponseEntity<ProductDTO> createProduct(@RequestBody Product product, Principal principal) {
        return ResponseEntity.ok(productService.createProduct(product, principal.getName()));
    }

    /**
     * Permite a atualização de um produto pelo ID (apenas o criador pode modificar)
     */
    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('SELLER', 'ADMIN')")
    public ResponseEntity<ProductDTO> updateProduct(@PathVariable Long id, @RequestBody Product productDetails, Principal principal) {
        return ResponseEntity.ok(productService.updateProduct(id, productDetails, principal.getName()));
    }

    /**
     * Permite gerenciar o estoque de um produto (apenas o criador pode modificar)
     */
    @PatchMapping("/{id}/stock")
    @PreAuthorize("hasAnyRole('SELLER', 'ADMIN')")
    public ResponseEntity<ProductDTO> updateProductStock(@PathVariable Long id, @RequestParam int quantity, Principal principal) {
        return ResponseEntity.ok(productService.updateProductStock(id, quantity, principal.getName()));
    }

    /**
     * Permite deletar um produto pelo ID (apenas o criador OU um ADMIN pode excluir)
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'SELLER')")
    public ResponseEntity<Void> deleteProduct(@PathVariable Long id, Principal principal) {
        productService.deleteProduct(id, principal.getName());
        return ResponseEntity.noContent().build();
    }
}
