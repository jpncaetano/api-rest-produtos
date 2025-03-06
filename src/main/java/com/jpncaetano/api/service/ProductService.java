package com.jpncaetano.api.service;

import com.jpncaetano.api.dto.ProductDTO;
import com.jpncaetano.api.enums.Role;
import com.jpncaetano.api.exception.ProductNotFoundException;
import com.jpncaetano.api.exception.UserNotAllowedException;
import com.jpncaetano.api.model.Product;
import com.jpncaetano.api.model.User;
import com.jpncaetano.api.repository.ProductRepository;
import com.jpncaetano.api.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ProductService {
    private final ProductRepository productRepository;
    private final UserRepository userRepository;

    public ProductService(ProductRepository productRepository, UserRepository userRepository) {
        this.productRepository = productRepository;
        this.userRepository = userRepository;
    }

    // Retorna todos os produtos cadastrados no sistema.
    public List<ProductDTO> getAllProducts() {
        return productRepository.findAll().stream()
                .map(ProductDTO::new)
                .collect(Collectors.toList());
    }

    // Busca um produto pelo ID.
    public ProductDTO getProductById(Long id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ProductNotFoundException("Produto com ID " + id + " não encontrado."));
        return new ProductDTO(product);
    }

    // Cria um novo produto associado ao usuário (SELLER/ ADMIN).
    public ProductDTO createProduct(Product product, String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado"));

        // Permite tanto SELLER quanto ADMIN criar produtos
        if (!(user.getRole().equals(Role.SELLER) || user.getRole().equals(Role.ADMIN))) {
            throw new RuntimeException("Apenas SELLERS e ADMINS podem cadastrar produtos.");
        }

        product.setCreatedBy(user);
        Product savedProduct = productRepository.save(product);

        return new ProductDTO(savedProduct);
    }

    // Atualiza os dados de um produto pelo ID.
    public ProductDTO updateProduct(Long id, Product productDetails) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ProductNotFoundException("Produto com ID " + id + " não encontrado."));

        product.setName(productDetails.getName());
        product.setDescription(productDetails.getDescription());
        product.setPrice(productDetails.getPrice());
        product.setQuantity(productDetails.getQuantity());

        Product updatedProduct = productRepository.save(product);
        return new ProductDTO(updatedProduct);
    }

    // Remove um produto do banco de dados.
    public void deleteProduct(Long id) {
        if (!productRepository.existsById(id)) {
            throw new ProductNotFoundException("Produto com ID " + id + " não encontrado.");
        }
        productRepository.deleteById(id);
    }
}
