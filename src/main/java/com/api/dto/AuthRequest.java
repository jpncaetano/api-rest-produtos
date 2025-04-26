package com.api.dto;

import com.api.enums.Role;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class AuthRequest {

    @Schema(description = "Nome de usuário para login ou cadastro", example = "joaopedro")
    @NotBlank(message = "O nome de usuário é obrigatório.")
    private String username;

    @Schema(description = "Senha do usuário", example = "senhaSegura123")
    @NotBlank(message = "A senha é obrigatória.")
    private String password;

    @Schema(description = "Tipo de usuário no sistema", example = "CUSTOMER")
    private Role role;
}