package com.jpncaetano.api.dto;

import com.jpncaetano.api.enums.Role;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AuthRequest {
    @NotBlank(message = "O nome de usuário é obrigatório.")
    private String username;

    @NotBlank(message = "A senha é obrigatória.")
    private String password;

    private Role role;
}
