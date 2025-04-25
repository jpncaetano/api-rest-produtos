
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
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
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
@Tag(name = "Autenticação", description = "Endpoints de login e registro de usuários")
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

    @Operation(summary = "Registro de usuário (CUSTOMER ou SELLER)")
    @ApiResponse(responseCode = "201", description = "Usuário registrado com sucesso")
    @PostMapping("/register")
    public ResponseEntity<String> register(@Valid @RequestBody AuthRequest request) {
        Role role = request.getRole();

        if ((role != Role.CUSTOMER && role != Role.SELLER)) {
            return ResponseEntity.badRequest().body("Tipo de usuário inválido. Escolha CUSTOMER ou SELLER.");
        }

        if (userRepository.findByUsername(request.getUsername()).isPresent()) {
            throw new UserAlreadyExistsException("Usuário já existe");
        }

        User user = new User(null, request.getUsername(), request.getPassword(), role);
        userService.save(user);
        return ResponseEntity.status(HttpStatus.CREATED).body("Usuário registrado com sucesso!");
    }

    @Operation(summary = "Registro de novo administrador (somente ADMINs)")
    @ApiResponse(responseCode = "201", description = "Usuário ADMIN registrado com sucesso")
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/register/admin")
    public ResponseEntity<String> registerAdmin(@Valid @RequestBody AuthRequest request) {
        User user = new User(null, request.getUsername(), request.getPassword(), Role.ADMIN);
        userService.save(user);
        return ResponseEntity.status(HttpStatus.CREATED).body("Usuário ADMIN registrado com sucesso!");
    }

    @Operation(summary = "Autenticação de usuário")
    @ApiResponse(responseCode = "200", description = "Login realizado com sucesso e token JWT gerado")
    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@RequestBody AuthRequest request) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword())
        );

        SecurityContextHolder.getContext().setAuthentication(authentication);

        User user = userRepository.findByUsername(request.getUsername())
                .orElseThrow(() -> new UserNotFoundException("Usuário não encontrado"));

        String token = jwtUtil.generateToken(user.getUsername(), user.getRole().name());

        return ResponseEntity.ok(new AuthResponse(token, user.getRole().name()));
    }

    @Operation(summary = "Logout do usuário autenticado")
    @ApiResponse(responseCode = "200", description = "Logout realizado com sucesso")
    @PostMapping("/logout")
    public ResponseEntity<String> logout() {
        SecurityContextHolder.clearContext();
        return ResponseEntity.ok("Logout realizado com sucesso.");
    }
}