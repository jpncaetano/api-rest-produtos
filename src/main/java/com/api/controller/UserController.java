package com.api.controller;

import com.api.dto.AuthRequest;
import com.api.dto.UserDTO;
import com.api.model.User;
import com.api.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("/users")
public class UserController {

    private final UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    // Métodos para Usuário Autenticado (CUSTOMER, SELLER, ADMIN)
    /**
     * Retorna os dados do usuário autenticado.
     */
    @GetMapping("/me")
    public ResponseEntity<User> getMyProfile(Principal principal) {
        return ResponseEntity.ok(userService.findByUsername(principal.getName()));
    }

    /**
     * Permite que um usuário autenticado atualize seu próprio perfil.
     */
    @PutMapping("/me")
    public ResponseEntity<String> updateMyProfile(@RequestBody AuthRequest request, Principal principal) {
        userService.updateUser(principal.getName(), request);
        return ResponseEntity.ok("Perfil atualizado com sucesso!");
    }

    /**
     * Permite que um usuário autenticado exclua sua própria conta.
     */
    @DeleteMapping("/me")
    public ResponseEntity<String> deleteMyAccount(Principal principal) {
        User authenticatedUser = userService.findByUsername(principal.getName());
        userService.deleteUserByUsername(principal.getName(), authenticatedUser);
        return ResponseEntity.ok("Conta excluída com sucesso!");
    }

    // Métodos Exclusivos para ADMIN
    /**
     * Permite que um ADMIN liste todos os usuários cadastrados.
     */
    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<UserDTO>> listAllUsers(Principal principal) {
        User authenticatedUser = userService.findByUsername(principal.getName());
        List<UserDTO> users = userService.findAll(authenticatedUser);
        return ResponseEntity.ok(users);
    }

    /**
     * Permite que um ADMIN busque um usuário específico pelo ID.
     */
    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<UserDTO> getUserById(@PathVariable Long id, Principal principal) {
        User authenticatedUser = userService.findByUsername(principal.getName());
        User user = userService.findById(id, authenticatedUser);
        return ResponseEntity.ok(new UserDTO(user.getId(), user.getUsername(), user.getRole()));
    }

    /**
     * Permite que um ADMIN exclua um usuário específico pelo ID.
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<String> deleteUserById(@PathVariable Long id, Principal principal) {
        User authenticatedUser = userService.findByUsername(principal.getName());
        userService.deleteUserById(id, authenticatedUser);
        return ResponseEntity.ok("Usuário excluído com sucesso!");
    }
}
