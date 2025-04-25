package com.api.dto;

import com.api.enums.Role;
import com.api.model.User;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class UserDTO {

    @Schema(description = "ID do usuário", example = "42")
    private Long id;

    @Schema(description = "Nome de usuário", example = "joaopedro")
    private String username;

    @Schema(description = "Tipo de papel do usuário no sistema", example = "ADMIN")
    private Role role;

    public UserDTO(User user) {
        if (user != null) {
            this.id = user.getId();
            this.username = user.getUsername();
            this.role = user.getRole();
        }
    }

    public UserDTO(Long id, String username, Role role) {
        this.id = id;
        this.username = username;
        this.role = role;
    }
}
