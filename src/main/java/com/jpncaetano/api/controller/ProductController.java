package com.jpncaetano.api.controller;

import com.jpncaetano.api.dto.ProductDTO;
import com.jpncaetano.api.model.Product;
import com.jpncaetano.api.service.ProductService;
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

    // Retorna a lista de todos os produtos cadastrados
    @GetMapping
    public ResponseEntity<List<ProductDTO>> getAllProducts() {
        return ResponseEntity.ok(productService.getAllProducts());
    }

    // Retorna um produto pelo ID, se encontrado
    @GetMapping("/{id}")
    public ResponseEntity<ProductDTO> getProductById(@PathVariable Long id) {
        return ResponseEntity.ok(productService.getProductById(id));
    }

    // Retorna os produtos cadastrados pelo próprio usuário autenticado (SELLER ou ADMIN)
    @GetMapping("/mine")
    @PreAuthorize("hasAnyRole('SELLER', 'ADMIN')")
    public ResponseEntity<List<ProductDTO>> getMyProducts(Principal principal) {
        return ResponseEntity.ok(productService.getMyProducts(principal.getName()));
    }

    // Retorna a lista de produtos ordenados por preço (ASC/DESC)
    @GetMapping(params = "sort")
    public ResponseEntity<List<ProductDTO>> getProductsSorted(@RequestParam String sort) {
        return ResponseEntity.ok(productService.getProductsSorted(sort));
    }

    // Permite a criação de um novo produto, apenas por SELLERs e ADMINs
    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'SELLER')")
    public ResponseEntity<ProductDTO> createProduct(@RequestBody Product product, Principal principal) {
        ProductDTO createdProduct = productService.createProduct(product, principal.getName());
        return ResponseEntity.ok(createdProduct);
    }

    // Permite a atualização de um produto pelo ID. Apenas SELLERs e ADMINs podem modificar produtos
    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('SELLER', 'ADMIN')")
    public ResponseEntity<ProductDTO> updateProduct(@PathVariable Long id, @RequestBody Product productDetails) {
        ProductDTO updatedProduct = productService.updateProduct(id, productDetails);
        return ResponseEntity.ok(updatedProduct);
    }

    // Permite gerenciar o estoque de um produto (adicionar/remover quantidade)
    @PatchMapping("/{id}/stock")
    @PreAuthorize("hasAnyRole('SELLER', 'ADMIN')")
    public ResponseEntity<ProductDTO> updateProductStock(@PathVariable Long id, @RequestParam int quantity) {
        return ResponseEntity.ok(productService.updateProductStock(id, quantity));
    }

    // Permite deletar um produto pelo ID. Apenas SELLERs e ADMINs podem excluir produtos
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'SELLER')")
    public ResponseEntity<Void> deleteProduct(@PathVariable Long id) {
        productService.deleteProduct(id);
        return ResponseEntity.noContent().build();
    }

    // Retorna a lista de produtos cadastrados por um vendedor específico
    @GetMapping(params = "sellerId")
    public ResponseEntity<List<ProductDTO>> getProductsBySeller(@RequestParam Long sellerId) {
        return ResponseEntity.ok(productService.getProductsBySeller(sellerId));
    }
}
