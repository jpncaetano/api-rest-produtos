package com.api.service;

import com.api.dto.ProductDTO;
import com.api.enums.Role;
import com.api.exception.ProductNotFoundException;
import com.api.model.Product;
import com.api.model.User;
import com.api.repository.ProductRepository;
import com.api.repository.UserRepository;
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

    // Métodos Públicos (Acesso Livre)
    /**
     * Retorna todos os produtos cadastrados no sistema (aberto para qualquer usuário).
     */
    public List<ProductDTO> getAllProducts() {
        return productRepository.findAll().stream()
                .map(ProductDTO::new)
                .collect(Collectors.toList());
    }

    /**
     * Retorna um produto pelo ID (acessível por qualquer usuário).
     */
    public ProductDTO getProductById(Long id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ProductNotFoundException("Produto com ID " + id + " não encontrado."));
        return new ProductDTO(product);
    }

    /**
     * Retorna a lista de produtos cadastrados por um seller ou admin específico.
     */
    public List<ProductDTO> getProductsByUser(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado."));

        // Retorna apenas produtos criados pelo usuário se ele for SELLER ou ADMIN
        if (user.getRole() != Role.SELLER && user.getRole() != Role.ADMIN) {
            throw new RuntimeException("Este usuário não pode cadastrar produtos.");
        }

        return productRepository.findByCreatedBy(user).stream()
                .map(ProductDTO::new)
                .collect(Collectors.toList());
    }

    /**
     * Retorna a lista de produtos ordenados por preço (ascendente ou descendente).
     */
    public List<ProductDTO> getProductsSorted(String sort) {
        if (!sort.equalsIgnoreCase("asc") && !sort.equalsIgnoreCase("desc")) {
            throw new IllegalArgumentException("O parâmetro de ordenação deve ser 'asc' ou 'desc'.");
        }
        return productRepository.findAllSortedByPrice(sort).stream()
                .map(ProductDTO::new)
                .collect(Collectors.toList());
    }


    // Métodos exclusivos para SELLERs e ADMINs
    /**
     * Cria um novo produto associado ao usuário autenticado (apenas SELLERs e ADMINs podem criar produtos).
     */
    public ProductDTO createProduct(Product product, String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado"));

        if (!(user.getRole().equals(Role.SELLER) || user.getRole().equals(Role.ADMIN))) {
            throw new RuntimeException("Apenas SELLERS e ADMINS podem cadastrar produtos.");
        }

        product.setCreatedBy(user);
        Product savedProduct = productRepository.save(product);
        return new ProductDTO(savedProduct);
    }

    /**
     * Retorna todos os produtos cadastrados pelo próprio usuário autenticado (SELLER ou ADMIN).
     */
    public List<ProductDTO> getMyProducts(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado"));
        return productRepository.findByCreatedBy(user).stream()
                .map(ProductDTO::new)
                .collect(Collectors.toList());
    }

    /**
     * Atualiza os dados de um produto pelo ID (apenas o criador pode modificar)
     */
    public ProductDTO updateProduct(Long id, Product productDetails, String username) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ProductNotFoundException("Produto com ID " + id + " não encontrado."));

        // Verifica se o usuário autenticado é o criador do produto
        if (!product.getCreatedBy().getUsername().equals(username)) {
            throw new RuntimeException("Você não tem permissão para modificar este produto.");
        }

        product.setName(productDetails.getName());
        product.setDescription(productDetails.getDescription());
        product.setPrice(productDetails.getPrice());
        product.setQuantity(productDetails.getQuantity());

        Product updatedProduct = productRepository.save(product);
        return new ProductDTO(updatedProduct);
    }

    /**
     * Atualiza o estoque de um produto pelo ID (apenas o criador pode modificar)
     */
    public ProductDTO updateProductStock(Long id, int quantity, String username) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ProductNotFoundException("Produto com ID " + id + " não encontrado."));

        System.out.println("Produto encontrado " + product);

        // Verifica se o usuário autenticado é o criador do produto
        if (!product.getCreatedBy().getUsername().equals(username)) {
            throw new RuntimeException("Você não tem permissão para modificar o estoque deste produto.");
        }


        product.setQuantity(product.getQuantity() + quantity);
        Product updatedProduct = productRepository.save(product);

        return new ProductDTO(updatedProduct);
    }

    // Remove um produto do banco de dados (apenas o criador ou um ADMIN pode excluir)
    public void deleteProduct(Long id, String username) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ProductNotFoundException("Produto com ID " + id + " não encontrado."));

        // Permite apenas o criador OU um ADMIN excluir o produto
        if (!product.getCreatedBy().getUsername().equals(username) && !isAdmin(username)) {
            throw new RuntimeException("Você não tem permissão para excluir este produto.");
        }

        productRepository.deleteById(id);
    }

    /**
     * Método auxiliar para verificar se o usuário é ADMIN
     */
    private boolean isAdmin(String username) {
        return userRepository.findByUsername(username)
                .map(user -> user.getRole().equals(Role.ADMIN))
                .orElse(false);
    }
}
