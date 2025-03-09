package com.api.dto;

import com.api.enums.Role;
import com.api.model.User;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class UserDTO {
    private Long id;
    private String username;
    private Role role;

    // Construtor que converte um objeto User em um UserDTO
    public UserDTO(User user) {
        if (user != null) {
            this.id = user.getId();
            this.username = user.getUsername();
            this.role = user.getRole();
        }
    }

    // Adicionando um construtor que aceita os atributos diretamente
    public UserDTO(Long id, String username, Role role) {
        this.id = id;
        this.username = username;
        this.role = role;
    }
}
