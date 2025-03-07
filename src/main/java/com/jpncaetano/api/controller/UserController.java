package com.jpncaetano.api.controller;

import com.jpncaetano.api.dto.AuthRequest;
import com.jpncaetano.api.dto.UserDTO;
import com.jpncaetano.api.model.User;
import com.jpncaetano.api.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/users")
public class UserController {

    private final UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    // Retorna os dados do usuário autenticado
    @GetMapping("/me")
    public ResponseEntity<User> getMyProfile(Principal principal) {
        return ResponseEntity.ok(userService.findByUsername(principal.getName()));
    }

    // Permite atualizar o perfil do usuário autenticado
    @PutMapping("/me")
    public ResponseEntity<String> updateMyProfile(@RequestBody AuthRequest request, Principal principal) {
        userService.updateUser(principal.getName(), request);
        return ResponseEntity.ok("Perfil atualizado com sucesso!");
    }

    // Permite excluir a própria conta
    @DeleteMapping("/me")
    public ResponseEntity<String> deleteMyAccount(Principal principal) {
        userService.deleteUserByUsername(principal.getName());
        return ResponseEntity.ok("Conta excluída com sucesso!");
    }

    // Permite que ADMIN liste todos os usuários cadastrados
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping
    public ResponseEntity<List<UserDTO>> listAllUsers() {
        List<UserDTO> users = userService.findAll()
                .stream()
                .map(user -> new UserDTO(user.getId(), user.getUsername(), user.getRole()))
                .collect(Collectors.toList());

        return ResponseEntity.ok(users);
    }

    // Permite que ADMIN busque um usuário pelo ID
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/{id}")
    public ResponseEntity<UserDTO> getUserById(@PathVariable Long id) {
        User user = userService.findById(id);
        return ResponseEntity.ok(new UserDTO(user.getId(), user.getUsername(), user.getRole()));
    }

    // Permite que ADMIN exclua um usuário pelo ID
    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteUserById(@PathVariable Long id) {
        userService.deleteUserById(id);
        return ResponseEntity.ok("Usuário excluído com sucesso!");
    }
}
