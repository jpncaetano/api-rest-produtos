package com.jpncaetano.api.service;

import com.jpncaetano.api.dto.AuthRequest;
import com.jpncaetano.api.dto.UserDTO;
import com.jpncaetano.api.exception.UserAlreadyExistsException;
import com.jpncaetano.api.exception.UserNotFoundException;
import com.jpncaetano.api.model.User;
import com.jpncaetano.api.repository.UserRepository;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public User save(User user) {
        if (userRepository.findByUsername(user.getUsername()).isPresent()) {
            throw new UserAlreadyExistsException("Usuário já existe: " + user.getUsername());
        }

        user.setPassword(passwordEncoder.encode(user.getPassword()));
        return userRepository.save(user);
    }

    public User findByUsername(String username) {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new UserNotFoundException("Usuário não encontrado: " + username));
    }

    public void updateUser(String username, AuthRequest request) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UserNotFoundException("Usuário não encontrado: " + username));

        // Evita alterar para um username já existente
        if (request.getUsername() != null && !request.getUsername().isEmpty() &&
                !request.getUsername().equals(user.getUsername()) &&
                userRepository.findByUsername(request.getUsername()).isPresent()) {
            throw new UserAlreadyExistsException("Nome de usuário já está em uso: " + request.getUsername());
        }

        if (request.getUsername() != null && !request.getUsername().isEmpty()) {
            user.setUsername(request.getUsername());
        }
        if (request.getPassword() != null && !request.getPassword().isEmpty()) {
            user.setPassword(passwordEncoder.encode(request.getPassword()));
        }

        userRepository.save(user);
    }

    public List<UserDTO> findAll() {
        return userRepository.findAll().stream()
                .map(UserDTO::new)
                .collect(Collectors.toList());
    }
}
