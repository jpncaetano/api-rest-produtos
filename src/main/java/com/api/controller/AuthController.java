package com.api.controller;

import com.api.dto.AuthRequest;
import com.api.dto.AuthResponse;
import com.api.enums.Role;
import com.api.exception.UserAlreadyExistsException;
import com.api.exception.UserNotFoundException;
import com.api.model.User;
import com.api.repository.UserRepository;
import com.api.security.JwtUtil;
import com.api.service.UserService;
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

        // Verifica se o usuário já existe antes de criar
        if (userRepository.findByUsername(request.getUsername()).isPresent()) {
            throw new UserAlreadyExistsException("Usuário já existe");
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
                .orElseThrow(() -> new UserNotFoundException("Usuário não encontrado"));

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
