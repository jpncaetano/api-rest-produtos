package com.api.controller;

import com.api.dto.AuthRequest;
import com.api.dto.UserDTO;
import com.api.enums.Role;
import com.api.exception.GlobalExceptionHandler;
import com.api.model.User;
import com.api.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;

import org.springframework.security.access.AccessDeniedException;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.security.Principal;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class UserControllerTest {

    private MockMvc mockMvc;

    @Mock
    private UserService userService;

    @InjectMocks
    private UserController userController;

    private User customer;
    private User seller;
    private User admin;
    private Principal customerPrincipal;
    private Principal sellerPrincipal;
    private Principal adminPrincipal;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(userController)
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();

        customer = new User(1L, "customerUser", "password", Role.CUSTOMER);
        seller = new User(2L, "sellerUser", "password", Role.SELLER);
        admin = new User(3L, "adminUser", "password", Role.ADMIN);

        customerPrincipal = () -> "customerUser";
        sellerPrincipal = () -> "sellerUser";
        adminPrincipal = () -> "adminUser";
    }

    @Test
    void deveRetornarPerfilDoUsuarioAutenticado() throws Exception {
        when(userService.findUserDTOByUsername("customerUser"))
                .thenReturn(new UserDTO(customer.getId(), customer.getUsername(), customer.getRole()));

        mockMvc.perform(get("/users/me").principal(customerPrincipal))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username").value("customerUser"))
                .andExpect(jsonPath("$.role").value("CUSTOMER"));
    }

    @Test
    void deveAtualizarPerfilDoUsuarioAutenticado() throws Exception {
        AuthRequest updateRequest = new AuthRequest("updatedUser", "newPassword", Role.CUSTOMER);
        doNothing().when(userService).updateUser(eq("customerUser"), any(AuthRequest.class));

        mockMvc.perform(put("/users/me")
                        .principal(customerPrincipal)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"username\": \"updatedUser\", \"password\": \"newPassword\"}"))
                .andExpect(status().isOk())
                .andExpect(content().string("Perfil atualizado com sucesso!"));
    }

    @Test
    void deveExcluirContaDoUsuarioAutenticado() throws Exception {
        when(userService.findByUsername("customerUser")).thenReturn(customer);
        doNothing().when(userService).deleteUserByUsername(eq("customerUser"), any(User.class));

        mockMvc.perform(delete("/users/me").principal(customerPrincipal))
                .andExpect(status().isOk())
                .andExpect(content().string("Conta excluída com sucesso!"));
    }

    @Test
    void naoDevePermitirQueCustomerOuSellerListemTodosOsUsuarios() throws Exception {
        when(userService.findByUsername("customerUser")).thenReturn(customer);
        when(userService.findByUsername("sellerUser")).thenReturn(seller);

        doThrow(new AccessDeniedException("Apenas administradores podem listar usuários."))
                .when(userService).findAll(customer);

        mockMvc.perform(get("/users").principal(customerPrincipal))
                .andExpect(status().isForbidden());

        doThrow(new AccessDeniedException("Apenas administradores podem listar usuários."))
                .when(userService).findAll(seller);

        mockMvc.perform(get("/users").principal(sellerPrincipal))
                .andExpect(status().isForbidden());
    }


    @Test
    void devePermitirQueAdminListeTodosOsUsuarios() throws Exception {
        when(userService.findByUsername("adminUser")).thenReturn(admin);
        when(userService.findAll(admin)).thenReturn(List.of(new UserDTO(customer), new UserDTO(seller)));

        mockMvc.perform(get("/users").principal(adminPrincipal))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size()").value(2));
    }

    @Test
    void devePermitirQueAdminBusqueUsuarioPorId() throws Exception {
        when(userService.findByUsername("adminUser")).thenReturn(admin);
        when(userService.findById(1L, admin)).thenReturn(customer);

        mockMvc.perform(get("/users/1").principal(adminPrincipal))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username").value("customerUser"));
    }

    @Test
    void devePermitirQueAdminExcluaUsuarioPorId() throws Exception {
        when(userService.findByUsername("adminUser")).thenReturn(admin);
        doNothing().when(userService).deleteUserById(1L, admin);

        mockMvc.perform(delete("/users/1").principal(adminPrincipal))
                .andExpect(status().isOk())
                .andExpect(content().string("Usuário excluído com sucesso!"));
    }

}
