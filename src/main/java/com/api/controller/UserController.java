
package com.api.controller;

import com.api.dto.AuthRequest;
import com.api.dto.UserDTO;
import com.api.model.User;
import com.api.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@Tag(name = "Usuários", description = "Endpoints para gerenciamento de contas de usuários")
@RestController
@RequestMapping("/users")
public class UserController {

    private final UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    // Métodos para Usuário Autenticado (CUSTOMER, SELLER, ADMIN)

    @Operation(summary = "Obtém o perfil do usuário autenticado")
    @ApiResponse(responseCode = "200", description = "Perfil retornado com sucesso")
    @GetMapping("/me")
    public ResponseEntity<User> getMyProfile(Principal principal) {
        return ResponseEntity.ok(userService.findByUsername(principal.getName()));
    }

    @Operation(summary = "Atualiza os dados do usuário autenticado")
    @ApiResponse(responseCode = "200", description = "Perfil atualizado com sucesso")
    @PutMapping("/me")
    public ResponseEntity<String> updateMyProfile(@RequestBody AuthRequest request, Principal principal) {
        userService.updateUser(principal.getName(), request);
        return ResponseEntity.ok("Perfil atualizado com sucesso!");
    }

    @Operation(summary = "Exclui a conta do usuário autenticado")
    @ApiResponse(responseCode = "200", description = "Conta excluída com sucesso")
    @DeleteMapping("/me")
    public ResponseEntity<String> deleteMyAccount(Principal principal) {
        User authenticatedUser = userService.findByUsername(principal.getName());
        userService.deleteUserByUsername(principal.getName(), authenticatedUser);
        return ResponseEntity.ok("Conta excluída com sucesso!");
    }

    // Métodos Exclusivos para ADMIN

    @Operation(summary = "Lista todos os usuários (apenas ADMIN)")
    @ApiResponse(responseCode = "200", description = "Lista de usuários retornada com sucesso")
    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<UserDTO>> listAllUsers(Principal principal) {
        User authenticatedUser = userService.findByUsername(principal.getName());
        List<UserDTO> users = userService.findAll(authenticatedUser);
        return ResponseEntity.ok(users);
    }

    @Operation(summary = "Busca usuário por ID (apenas ADMIN)")
    @ApiResponse(responseCode = "200", description = "Usuário encontrado com sucesso")
    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<UserDTO> getUserById(@PathVariable Long id, Principal principal) {
        User authenticatedUser = userService.findByUsername(principal.getName());
        User user = userService.findById(id, authenticatedUser);
        return ResponseEntity.ok(new UserDTO(user.getId(), user.getUsername(), user.getRole()));
    }

    @Operation(summary = "Exclui um usuário pelo ID (apenas ADMIN)")
    @ApiResponse(responseCode = "200", description = "Usuário excluído com sucesso")
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<String> deleteUserById(@PathVariable Long id, Principal principal) {
        User authenticatedUser = userService.findByUsername(principal.getName());
        userService.deleteUserById(id, authenticatedUser);
        return ResponseEntity.ok("Usuário excluído com sucesso!");
    }
}