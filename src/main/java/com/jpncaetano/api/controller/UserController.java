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

    @GetMapping("/me")
    public ResponseEntity<User> getMyProfile(Principal principal) {
        return ResponseEntity.ok(userService.findByUsername(principal.getName()));
    }

    @PutMapping("/me")
    public ResponseEntity<String> updateMyProfile(@RequestBody AuthRequest request, Principal principal) {
        userService.updateUser(principal.getName(), request);
        return ResponseEntity.ok("Perfil atualizado com sucesso!");
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping
    public ResponseEntity<List<UserDTO>> listAllUsers() {
        List<UserDTO> users = userService.findAll()
                .stream()
                .map(UserDTO::new)
                .collect(Collectors.toList());
        return ResponseEntity.ok(users);
    }


}

