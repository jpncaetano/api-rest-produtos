package com.api.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class AuthResponse {

    @Schema(description = "Token JWT gerado após autenticação", example = "eyJhbGciOiJIUzI1NiIsInR5...")
    private String token;

    @Schema(description = "Papel do usuário autenticado", example = "SELLER")
    private String role;
}