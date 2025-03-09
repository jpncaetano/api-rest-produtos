package com.api.controller;

import com.api.dto.AuthRequest;
import com.api.dto.AuthResponse;
import com.api.enums.Role;
import com.api.exception.GlobalExceptionHandler;
import com.api.exception.UserNotFoundException;
import com.api.model.User;
import com.api.repository.UserRepository;
import com.api.security.JwtUtil;
import com.api.service.UserService;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.Optional;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class AuthControllerTest {

    private MockMvc mockMvc;

    @Mock
    private UserService userService;

    @Mock
    private JwtUtil jwtUtil;

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private AuthController authController;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(authController)
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();
    }


    @Test
    void shouldRegisterUserSuccessfully() throws Exception {
        AuthRequest request = new AuthRequest("user", "password", Role.CUSTOMER);
        when(userService.save(any(User.class))).thenReturn(new User());

        mockMvc.perform(post("/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"username\":\"user\",\"password\":\"password\",\"role\":\"CUSTOMER\"}"))
                .andExpect(status().isCreated())
                .andExpect(content().string("Usuário registrado com sucesso!"));
    }

    @Test
    void shouldNotRegisterInvalidRole() throws Exception {
        AuthRequest request = new AuthRequest("user", "password", null);

        mockMvc.perform(post("/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"username\":\"user\",\"password\":\"password\",\"role\":null}"))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("Tipo de usuário inválido. Escolha CUSTOMER ou SELLER."));
    }

    @Test
    void shouldRegisterAdminSuccessfully() throws Exception {
        AuthRequest request = new AuthRequest("admin", "password", Role.ADMIN);
        when(userService.save(any(User.class))).thenReturn(new User());

        mockMvc.perform(post("/auth/register/admin")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"username\":\"admin\",\"password\":\"password\"}"))
                .andExpect(status().isCreated())
                .andExpect(content().string("Usuário ADMIN registrado com sucesso!"));
    }

    @Test
    void shouldAuthenticateUserAndReturnToken() throws Exception {
        AuthRequest request = new AuthRequest("user", "password", Role.CUSTOMER);
        Authentication authentication = mock(Authentication.class);
        User user = new User(1L, "user", "password", Role.CUSTOMER);
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenReturn(authentication);
        when(userRepository.findByUsername("user")).thenReturn(Optional.of(user));
        when(jwtUtil.generateToken("user", "CUSTOMER")).thenReturn("mocked-jwt-token");

        mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"username\":\"user\",\"password\":\"password\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").value("mocked-jwt-token"))
                .andExpect(jsonPath("$.role").value("CUSTOMER"));
    }

    @Test
    void shouldFailAuthenticationForInvalidUser() throws Exception {
        when(userRepository.findByUsername("invalid")).thenReturn(Optional.empty());

        mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"username\":\"invalid\",\"password\":\"password\"}"))
                .andExpect(status().isUnauthorized())  // Verifica se retorna HTTP 401
                .andExpect(jsonPath("$.message").value("Usuário não encontrado")); // Verifica a mensagem correta
    }


    @Test
    void shouldLogoutSuccessfully() throws Exception {
        mockMvc.perform(post("/auth/logout"))
                .andExpect(status().isOk())
                .andExpect(content().string("Logout realizado com sucesso."));
    }

    @Test
    void shouldNotRegisterDuplicateUser() throws Exception {
        AuthRequest request = new AuthRequest("existingUser", "password", Role.CUSTOMER);

        // Simula que o usuário já existe no banco de dados
        when(userRepository.findByUsername("existingUser")).thenReturn(Optional.of(new User()));

        mockMvc.perform(post("/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"username\":\"existingUser\",\"password\":\"password\",\"role\":\"CUSTOMER\"}"))
                .andExpect(status().isConflict())  // Espera 409 Conflict
                .andExpect(jsonPath("$.message").value("Usuário já existe"));
    }

    @Test
    void shouldFailLoginWithIncorrectPassword() throws Exception {
        // Simula erro de autenticação (senha incorreta)
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenThrow(new BadCredentialsException("Credenciais inválidas"));

        mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"username\":\"validUser\",\"password\":\"wrongPassword\"}"))
                .andExpect(status().isUnauthorized())  // Espera 401 Unauthorized
                .andExpect(jsonPath("$.message").value(Matchers.containsString("Usuário ou senha incorretos")));
    }


}
