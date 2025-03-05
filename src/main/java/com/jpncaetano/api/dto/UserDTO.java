package com.jpncaetano.api.dto;

import com.jpncaetano.api.enums.Role;
import com.jpncaetano.api.model.User;
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

    public UserDTO(User user) {
        if (user != null) {
            this.id = user.getId();
            this.username = user.getUsername();
            this.role = user.getRole();
        }
    }
}
