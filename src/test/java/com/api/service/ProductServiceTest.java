package com.api.service;

import com.api.dto.ProductDTO;
import com.api.enums.Role;
import com.api.exception.ProductNotFoundException;
import com.api.model.Product;
import com.api.model.User;
import com.api.repository.ProductRepository;
import com.api.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ProductServiceTest {

    @Mock
    private ProductRepository productRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private ProductService productService;

    private User sellerUser;
    private User adminUser;
    private User customerUser;
    private Product product;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        // Configuração de usuários de teste
        sellerUser = new User(1L, "sellerUser", "password", Role.SELLER);
        adminUser = new User(2L, "adminUser", "password", Role.ADMIN);
        customerUser = new User(3L, "customerUser", "password", Role.CUSTOMER);

        // Configuração de um produto vinculado ao sellerUser
        product = new Product(1L, "Produto Teste", "Descrição", new BigDecimal("100.0"), 10, sellerUser);

        // Mocks do repositório de produtos e usuários
        when(productRepository.findById(1L)).thenReturn(Optional.of(product));
        when(userRepository.findByUsername(sellerUser.getUsername())).thenReturn(Optional.of(sellerUser));
        when(userRepository.findByUsername(adminUser.getUsername())).thenReturn(Optional.of(adminUser));
        when(userRepository.findByUsername(customerUser.getUsername())).thenReturn(Optional.of(customerUser));
        when(productRepository.save(any(Product.class))).thenAnswer(invocation -> invocation.getArgument(0));
    }

    @Test
    void deveBuscarTodosOsProdutos() {
        when(productRepository.findAll(any(Pageable.class)))
                .thenReturn(new PageImpl<>(List.of(product), PageRequest.of(0, 10), 1));

        Page<ProductDTO> products = productService.getAllProducts(0, 10, new String[]{"name", "asc"});

        assertFalse(products.isEmpty());
        assertEquals(1, products.getContent().size());
        assertEquals("Produto Teste", products.getContent().get(0).getName());
    }


    @Test
    void deveBuscarProdutoPorId() {
        ProductDTO result = productService.getProductById(1L);

        assertNotNull(result);
        assertEquals(1L, result.getId());
    }

    @Test
    void deveLancarExcecaoAoBuscarProdutoInexistente() {
        when(productRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(ProductNotFoundException.class, () -> productService.getProductById(99L));
    }

    @Test
    void deveBuscarProdutosPorUsuarioQuePodeCadastrarProdutos() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(sellerUser));
        when(userRepository.findById(2L)).thenReturn(Optional.of(adminUser));

        when(productRepository.findByCreatedBy(sellerUser)).thenReturn(List.of(product));
        when(productRepository.findByCreatedBy(adminUser)).thenReturn(List.of(product));

        List<ProductDTO> sellerProducts = productService.getProductsByUser(1L);
        List<ProductDTO> adminProducts = productService.getProductsByUser(2L);

        assertFalse(sellerProducts.isEmpty(), "A lista de produtos do sellerUser não deveria estar vazia!");
        assertFalse(adminProducts.isEmpty(), "A lista de produtos do adminUser não deveria estar vazia!");
    }


    @Test
    void deveLancarExcecaoAoBuscarProdutosDeUsuarioInexistente() {
        when(userRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> productService.getProductsByUser(99L));
    }

    @Test
    void deveListarProdutosOrdenadosPorPreco() {
        when(productRepository.findAllSortedByPrice("asc")).thenReturn(List.of(product));

        List<ProductDTO> products = productService.getProductsSorted("asc");

        assertFalse(products.isEmpty());
        assertEquals("Produto Teste", products.get(0).getName());
    }

    @Test
    void deveLancarExcecaoAoOrdenarComParametroInvalido() {
        assertThrows(IllegalArgumentException.class, () -> productService.getProductsSorted("invalid"));
    }

    @Test
    void deveCriarProdutoComPermissao() {
        when(productRepository.save(any(Product.class))).thenReturn(product);

        ProductDTO createdProduct = productService.createProduct(product, "sellerUser");

        assertNotNull(createdProduct);
        verify(productRepository, times(1)).save(any(Product.class));
    }

    @Test
    void deveLancarExcecaoAoCriarProdutoComoCustomer() {
        assertThrows(RuntimeException.class, () -> productService.createProduct(product, "customerUser"));
    }

    @Test
    void deveBuscarProdutosDoProprioUsuario() {
        when(productRepository.findByCreatedBy(sellerUser)).thenReturn(List.of(product));

        List<ProductDTO> products = productService.getMyProducts("sellerUser");

        assertFalse(products.isEmpty());
        assertEquals(1, products.size());
    }

    @Test
    void deveAtualizarProdutoApenasPeloCriador() {
        Product updatedDetails = new Product(1L, "Novo Nome", "Nova Descrição", new BigDecimal("120.0"), 15, sellerUser);

        assertDoesNotThrow(() -> productService.updateProduct(1L, updatedDetails, sellerUser.getUsername()));

        verify(productRepository, times(1)).save(product);
        assertEquals("Novo Nome", product.getName());
        assertEquals("Nova Descrição", product.getDescription());
        assertEquals(new BigDecimal("120.0"), product.getPrice());
        assertEquals(15, product.getQuantity());
    }

    @Test
    void deveLancarExcecaoAoAtualizarProdutoDeOutroUsuario() {
        Product updatedDetails = new Product();
        updatedDetails.setName("Novo Nome");

        assertThrows(RuntimeException.class, () -> productService.updateProduct(1L, updatedDetails, "outroUsuario"));
    }

    @Test
    void deveAtualizarEstoqueApenasPeloCriador() {
        assertDoesNotThrow(() -> productService.updateProductStock(1L, 5, sellerUser.getUsername()));

        verify(productRepository, times(1)).save(product);
        assertEquals(15, product.getQuantity());
    }

    @Test
    void deveLancarExcecaoAoAtualizarEstoqueDeOutroUsuario() {
        assertThrows(RuntimeException.class, () -> productService.updateProductStock(1L, 5, "outroUsuario"));
    }

    @Test
    void deveExcluirProdutoPeloCriadorOuAdmin() {
        assertDoesNotThrow(() -> productService.deleteProduct(1L, sellerUser.getUsername()));
        verify(productRepository, times(1)).deleteById(1L);

        assertDoesNotThrow(() -> productService.deleteProduct(1L, adminUser.getUsername()));
        verify(productRepository, times(2)).deleteById(1L);
    }

    @Test
    void deveLancarExcecaoAoExcluirProdutoDeOutroUsuario() {
        assertThrows(RuntimeException.class, () -> productService.deleteProduct(1L, "outroUsuario"));
    }

    @Test
    void deveLancarExcecaoAoExcluirProdutoInexistente() {
        when(productRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(ProductNotFoundException.class, () -> productService.deleteProduct(99L, sellerUser.getUsername()));
    }
}
