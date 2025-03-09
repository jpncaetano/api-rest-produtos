package com.jpncaetano.api.service;

import com.jpncaetano.api.dto.ProductDTO;
import com.jpncaetano.api.enums.Role;
import com.jpncaetano.api.exception.ProductNotFoundException;
import com.jpncaetano.api.model.Product;
import com.jpncaetano.api.model.User;
import com.jpncaetano.api.repository.ProductRepository;
import com.jpncaetano.api.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.math.BigDecimal;
import java.util.Arrays;
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

        // Criando usuários de teste
        sellerUser = new User(1L, "sellerUser", "password", Role.SELLER);
        adminUser = new User(2L, "adminUser", "password", Role.ADMIN);
        customerUser = new User(3L, "customerUser", "password", Role.CUSTOMER);

        // Criando um produto vinculado ao sellerUser
        product = new Product(1L, "Produto Teste", "Descrição", new BigDecimal("100.0"), 10, sellerUser);

        // Configuração do mock para encontrar o produto pelo ID
        when(productRepository.findById(1L)).thenReturn(Optional.of(product));

        // Configuração dos mocks para simular o comportamento esperado
        when(userRepository.findByUsername(sellerUser.getUsername())).thenReturn(Optional.of(sellerUser));
        when(userRepository.findByUsername(adminUser.getUsername())).thenReturn(Optional.of(adminUser));
        when(userRepository.findByUsername(customerUser.getUsername())).thenReturn(Optional.of(customerUser));

    }


    /**
     * Deve buscar todos os produtos cadastrados
     */
    @Test
    void deveBuscarTodosOsProdutos() {
        when(productRepository.findAll()).thenReturn(Arrays.asList(product));

        List<ProductDTO> products = productService.getAllProducts();

        assertFalse(products.isEmpty());
        assertEquals(1, products.size());
        assertEquals("Produto Teste", products.get(0).getName());
    }

    /**
     * Deve buscar um produto por ID
     */
    @Test
    void deveBuscarProdutoPorId() {
        when(productRepository.findById(1L)).thenReturn(Optional.of(product));

        ProductDTO result = productService.getProductById(1L);

        assertNotNull(result);
        assertEquals(1L, result.getId());
    }

    /**
     * Deve lançar exceção ao buscar produto inexistente
     */
    @Test
    void deveLancarExcecaoAoBuscarProdutoInexistente() {
        when(productRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(ProductNotFoundException.class, () -> productService.getProductById(1L));
    }

    /**
     * Deve buscar produtos cadastrados por um SELLER ou ADMIN
     */
    @Test
    void deveBuscarProdutosPorUsuarioQuePodeCadastrarProdutos() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(sellerUser));
        when(userRepository.findById(2L)).thenReturn(Optional.of(adminUser));
        when(productRepository.findByCreatedBy(sellerUser)).thenReturn(Arrays.asList(product));
        when(productRepository.findByCreatedBy(adminUser)).thenReturn(Arrays.asList(product));

        List<ProductDTO> sellerProducts = productService.getProductsByUser(1L);
        List<ProductDTO> adminProducts = productService.getProductsByUser(2L);

        assertFalse(sellerProducts.isEmpty());
        assertFalse(adminProducts.isEmpty());
    }

    /**
     * Deve lançar exceção ao buscar produtos de um usuário inexistente
     */
    @Test
    void deveLancarExcecaoAoBuscarProdutosDeUsuarioInexistente() {
        when(userRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> productService.getProductsByUser(99L));
    }

    /**
     * Deve listar produtos ordenados por preço
     */
    @Test
    void deveListarProdutosOrdenadosPorPreco() {
        when(productRepository.findAllSortedByPrice("asc")).thenReturn(Arrays.asList(product));

        List<ProductDTO> products = productService.getProductsSorted("asc");

        assertFalse(products.isEmpty());
        assertEquals("Produto Teste", products.get(0).getName());
    }

    /**
     * Deve lançar exceção ao passar parâmetro inválido para ordenação
     */
    @Test
    void deveLancarExcecaoAoOrdenarComParametroInvalido() {
        assertThrows(IllegalArgumentException.class, () -> productService.getProductsSorted("invalid"));
    }

    /**
     * Deve permitir que SELLER ou ADMIN crie produto
     */
    @Test
    void deveCriarProdutoComPermissao() {
        when(userRepository.findByUsername("sellerUser")).thenReturn(Optional.of(sellerUser));
        when(productRepository.save(any(Product.class))).thenReturn(product);

        ProductDTO createdProduct = productService.createProduct(product, "sellerUser");

        assertNotNull(createdProduct);
        verify(productRepository, times(1)).save(any(Product.class));
    }

    /**
     * Deve lançar exceção ao tentar criar produto como CUSTOMER
     */
    @Test
    void deveLancarExcecaoAoCriarProdutoComoCustomer() {
        when(userRepository.findByUsername("customerUser")).thenReturn(Optional.of(customerUser));

        assertThrows(RuntimeException.class, () -> productService.createProduct(product, "customerUser"));
    }

    /**
     * Deve buscar produtos do próprio usuário autenticado
     */
    @Test
    void deveBuscarProdutosDoProprioUsuario() {
        when(userRepository.findByUsername("sellerUser")).thenReturn(Optional.of(sellerUser));
        when(productRepository.findByCreatedBy(sellerUser)).thenReturn(Arrays.asList(product));

        List<ProductDTO> products = productService.getMyProducts("sellerUser");

        assertFalse(products.isEmpty());
        assertEquals(1, products.size());
    }

    /**
     * Deve atualizar produto apenas pelo criador
     */
    @Test
    void deveAtualizarProdutoApenasPeloCriador() {
        Optional<Product> foundProduct = productRepository.findById(1L);
        assertTrue(foundProduct.isPresent(), "Produto não foi encontrado no repositório mockado!");

        assertNotNull(product, "Produto não deveria ser nulo antes do teste!");
        when(productRepository.findById(1L)).thenReturn(Optional.of(product));
        when(productRepository.save(any(Product.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Product updatedDetails = new Product(1L, "Novo Nome", "Nova Descrição", new BigDecimal("120.0"), 15, sellerUser);

        assertDoesNotThrow(() -> productService.updateProduct(1L, updatedDetails, sellerUser.getUsername()));

        verify(productRepository, times(1)).save(product);

        assertEquals("Novo Nome", product.getName());
        assertEquals("Nova Descrição", product.getDescription());
        assertEquals(new BigDecimal("120.0"), product.getPrice());
        assertEquals(15, product.getQuantity());
        assertEquals(sellerUser, product.getCreatedBy());
    }

    @Test
    void deveLancarExcecaoAoAtualizarProdutoDeOutroUsuario() {
        when(productRepository.findById(1L)).thenReturn(Optional.of(product));

        Product updatedDetails = new Product();
        updatedDetails.setName("Novo Nome");

        assertThrows(RuntimeException.class, () -> productService.updateProduct(1L, updatedDetails, "outroUsuario"));
    }

    /**
     * Deve atualizar estoque apenas pelo criador
     */
    @Test
    void deveAtualizarEstoqueApenasPeloCriador() {
        when(productRepository.findById(1L)).thenReturn(Optional.of(product));

        assertDoesNotThrow(() -> productService.updateProductStock(1L, 5, sellerUser.getUsername()));

        verify(productRepository, times(1)).save(product);

        assertEquals(15, product.getQuantity());
    }


    @Test
    void deveLancarExcecaoAoAtualizarEstoqueDeOutroUsuario() {
        when(productRepository.findById(1L)).thenReturn(Optional.of(product));

        assertThrows(RuntimeException.class, () -> productService.updateProductStock(1L, 5, "outroUsuario"));
    }

    /**
     * Deve excluir produto pelo criador ou ADMIN
     */
    @Test
    void deveExcluirProdutoPeloCriadorOuAdmin() {
        when(productRepository.findById(1L)).thenReturn(Optional.of(product));

        assertDoesNotThrow(() -> productService.deleteProduct(1L, sellerUser.getUsername()));
        verify(productRepository, times(1)).deleteById(1L);

        when(productRepository.findById(1L)).thenReturn(Optional.of(product));

        assertDoesNotThrow(() -> productService.deleteProduct(1L, adminUser.getUsername()));
        verify(productRepository, times(2)).deleteById(1L);
    }

    /**
     * Deve lançar exceção ao excluir produto de outro usuário
     */
    @Test
    void deveLancarExcecaoAoExcluirProdutoDeOutroUsuario() {
        when(productRepository.findById(1L)).thenReturn(Optional.of(product));

        assertThrows(RuntimeException.class, () -> productService.deleteProduct(1L, "outroUsuario"));
    }

    /**
     * Deve lançar exceção ao excluir produto inexistente
     */
    @Test
    void deveLancarExcecaoAoExcluirProdutoInexistente() {
        when(productRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(ProductNotFoundException.class, () -> productService.deleteProduct(1L, sellerUser.getUsername()));
    }
}
