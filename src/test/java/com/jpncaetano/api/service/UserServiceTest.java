package com.jpncaetano.api.service;

import com.jpncaetano.api.dto.AuthRequest;
import com.jpncaetano.api.enums.Role;
import com.jpncaetano.api.exception.UserAlreadyExistsException;
import com.jpncaetano.api.exception.UserNotFoundException;
import com.jpncaetano.api.model.User;
import com.jpncaetano.api.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserService userService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    // Testes para VISITANTE (Sem autenticação)
    @Test
    void deveSalvarUsuarioComSenhaCriptografada() {
        User user = new User(null, "testuser", "password", Role.CUSTOMER);
        when(userRepository.findByUsername("testuser")).thenReturn(Optional.empty());
        when(passwordEncoder.encode("password")).thenReturn("hashedpassword");
        when(userRepository.save(any(User.class))).thenReturn(user);

        User savedUser = userService.save(user);

        assertNotNull(savedUser);
        assertEquals("hashedpassword", savedUser.getPassword());
        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    void deveLancarExcecaoAoSalvarUsuarioJaExistente() {
        User user = new User(null, "testuser", "password", Role.CUSTOMER);
        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(user));

        assertThrows(UserAlreadyExistsException.class, () -> userService.save(user));
    }

    // Testes para CUSTOMER, SELLER e ADMIN
    @ParameterizedTest
    @EnumSource(Role.class)
    void deveBuscarUsuarioPorUsernameComTodosOsRoles(Role role) {
        User user = new User(1L, "testuser", "password", role);
        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(user));

        User foundUser = userService.findByUsername("testuser");

        assertNotNull(foundUser);
        assertEquals(role, foundUser.getRole());
    }

    @ParameterizedTest
    @EnumSource(Role.class)
    void deveAtualizarUsuario(Role role) {
        User user = new User(1L, "testuser", "password", role);
        AuthRequest request = new AuthRequest("newuser", "newpassword", role);
        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(user));
        when(userRepository.findByUsername("newuser")).thenReturn(Optional.empty());
        when(passwordEncoder.encode("newpassword")).thenReturn("hashedpassword");

        userService.updateUser("testuser", request);

        assertEquals("newuser", user.getUsername());
        assertEquals("hashedpassword", user.getPassword());
    }

    @ParameterizedTest
    @EnumSource(Role.class)
    void deveLancarExcecaoAoAtualizarUsuarioParaNomeJaExistente(Role role) {
        User user = new User(1L, "testuser", "password", role);
        AuthRequest request = new AuthRequest("existinguser", "password", role);
        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(user));
        when(userRepository.findByUsername("existinguser")).thenReturn(Optional.of(new User()));

        assertThrows(UserAlreadyExistsException.class, () -> userService.updateUser("testuser", request));
    }

    @ParameterizedTest
    @EnumSource(Role.class)
    void deveExcluirUsuarioPorUsername(Role role) {
        User user = new User(1L, "testuser", "password", role);

        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(user));
        doNothing().when(userRepository).delete(user);

        userService.deleteUserByUsername("testuser", user);

        verify(userRepository, times(1)).delete(user);
    }

    @ParameterizedTest
    @EnumSource(Role.class) // Testando CUSTOMER, SELLER e ADMIN
    void deveLancarExcecaoAoExcluirOutroUsuario(Role role) {
        User authenticatedUser = new User(1L, "authuser", "password", role);
        User anotherUser = new User(2L, "otheruser", "password", Role.CUSTOMER);

        when(userRepository.findByUsername("otheruser")).thenReturn(Optional.of(anotherUser));

        assertThrows(AccessDeniedException.class, () -> userService.deleteUserByUsername("otheruser", authenticatedUser));
    }

    // Testes para ADMIN (Apenas ADMIN pode listar/deletar usuários por ID)
    @Test
    void deveLancarExcecaoAoBuscarUsuarioInexistente() {
        User adminUser = new User(99L, "admin", "password", Role.ADMIN);

        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class, () -> userService.findById(1L, adminUser));
    }

    @Test
    void deveExcluirUsuarioPorId() {
        User adminUser = new User(99L, "admin", "password", Role.ADMIN);

        when(userRepository.existsById(1L)).thenReturn(true);
        doNothing().when(userRepository).deleteById(1L);

        userService.deleteUserById(1L, adminUser);

        verify(userRepository, times(1)).deleteById(1L);
    }

    @Test
    void deveLancarExcecaoAoExcluirUsuarioInexistente() {
        User adminUser = new User(99L, "admin", "password", Role.ADMIN);

        when(userRepository.existsById(1L)).thenReturn(false);

        assertThrows(UserNotFoundException.class, () -> userService.deleteUserById(1L, adminUser));
    }

    @Test
    void deveLancarExcecaoAoExcluirUsuarioSemPermissao() {
        User nonAdminUser = new User(1L, "user", "password", Role.CUSTOMER);

        assertThrows(AccessDeniedException.class, () -> userService.deleteUserById(1L, nonAdminUser));
    }

}
