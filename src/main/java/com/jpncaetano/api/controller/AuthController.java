package com.jpncaetano.api.controller;

import com.jpncaetano.api.dto.AuthRequest;
import com.jpncaetano.api.dto.AuthResponse;
import com.jpncaetano.api.enums.Role;
import com.jpncaetano.api.model.User;
import com.jpncaetano.api.repository.UserRepository;
import com.jpncaetano.api.security.JwtUtil;
import com.jpncaetano.api.service.UserService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private final UserService userService;
    private final JwtUtil jwtUtil;
    private final AuthenticationManager authenticationManager;
    private final UserRepository userRepository;

    public AuthController(UserService userService, JwtUtil jwtUtil,
                          AuthenticationManager authenticationManager, UserRepository userRepository) {
        this.userService = userService;
        this.jwtUtil = jwtUtil;
        this.authenticationManager = authenticationManager;
        this.userRepository = userRepository;
    }

    // Registra um novo usuário do tipo CUSTOMER ou SELLER
    @PostMapping("/register")
    public ResponseEntity<String> register(@Valid @RequestBody AuthRequest request) {
        Role role = request.getRole();

        if ((role != Role.CUSTOMER && role != Role.SELLER)) {
            return ResponseEntity.badRequest().body("Tipo de usuário inválido. Escolha CUSTOMER ou SELLER.");
        }

        User user = new User(null, request.getUsername(), request.getPassword(), role);
        userService.save(user);
        return ResponseEntity.status(HttpStatus.CREATED).body("Usuário registrado com sucesso!");
    }

    // Registra um novo ADMIN (apenas ADMINs podem acessar)
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/register/admin")
    public ResponseEntity<String> registerAdmin(@Valid @RequestBody AuthRequest request) {
        User user = new User(null, request.getUsername(), request.getPassword(), Role.ADMIN);
        userService.save(user);
        return ResponseEntity.status(HttpStatus.CREATED).body("Usuário ADMIN registrado com sucesso!");
    }

    // Autentica um usuário e gera um token JWT
    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@RequestBody AuthRequest request) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword())
        );

        SecurityContextHolder.getContext().setAuthentication(authentication);

        // Buscar usuário no banco
        User user = userRepository.findByUsername(request.getUsername())
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado"));

        // Gerar token incluindo a role
        String token = jwtUtil.generateToken(user.getUsername(), user.getRole().name());

        return ResponseEntity.ok(new AuthResponse(token, user.getRole().name()));
    }

    // Realiza logout limpando o contexto de autenticação
    @PostMapping("/logout")
    public ResponseEntity<String> logout() {
        SecurityContextHolder.clearContext();
        return ResponseEntity.ok("Logout realizado com sucesso.");
    }
}
