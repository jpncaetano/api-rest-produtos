package com.api.dto;

import com.api.enums.Role;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class AuthRequest {
    @NotBlank(message = "O nome de usuário é obrigatório.")
    private String username;

    @NotBlank(message = "A senha é obrigatória.")
    private String password;

    private Role role;
}
