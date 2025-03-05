package com.jpncaetano.api.service;

import com.jpncaetano.api.dto.ProductDTO;
import com.jpncaetano.api.enums.Role;
import com.jpncaetano.api.model.Product;
import com.jpncaetano.api.model.User;
import com.jpncaetano.api.repository.ProductRepository;
import com.jpncaetano.api.repository.UserRepository;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class ProductService {
    private final ProductRepository productRepository;
    private final UserRepository userRepository; // Adicione essa linha

    public ProductService(ProductRepository productRepository, UserRepository userRepository) {
        this.productRepository = productRepository;
        this.userRepository = userRepository; // Inicialize aqui
    }

    public List<ProductDTO> getAllProducts() {
        return productRepository.findAll().stream()
                .map(ProductDTO::new)
                .collect(Collectors.toList());
    }


    public Optional<Product> getProductById(Long id) {
        return productRepository.findById(id);
    }

    public ProductDTO createProduct(Product product, String username) {
        User seller = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado"));

        if (!seller.getRole().equals(Role.SELLER)) {
            throw new RuntimeException("Apenas SELLERS podem cadastrar produtos.");
        }

        product.setCreatedBy(seller);
        Product savedProduct = productRepository.save(product);

        return new ProductDTO(savedProduct); // Agora retorna o DTO corretamente
    }




    public Product updateProduct(Long id, Product productDetails) {
        return productRepository.findById(id).map(product -> {
            product.setName(productDetails.getName());
            product.setDescription(productDetails.getDescription());
            product.setPrice(productDetails.getPrice());
            product.setQuantity(productDetails.getQuantity());
            return productRepository.save(product);
        }).orElseThrow(() -> new RuntimeException("Product not found"));
    }

    public void deleteProduct(Long id) {
        productRepository.deleteById(id);
    }
}
