package com.jpncaetano.api.service;

import com.jpncaetano.api.dto.AuthRequest;
import com.jpncaetano.api.dto.UserDTO;
import com.jpncaetano.api.enums.Role;
import com.jpncaetano.api.exception.UserAlreadyExistsException;
import com.jpncaetano.api.exception.UserNotFoundException;
import com.jpncaetano.api.model.User;
import com.jpncaetano.api.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.security.access.AccessDeniedException;

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

    // ==============================
    // 🔹 Métodos de VISITANTE (Sem autenticação)
    // ==============================

    // Salva um novo usuário no banco de dados
    public User save(User user) {
        if (userRepository.findByUsername(user.getUsername()).isPresent()) {
            throw new UserAlreadyExistsException("Usuário já existe: " + user.getUsername());
        }

        user.setPassword(passwordEncoder.encode(user.getPassword()));
        return userRepository.save(user);
    }

    // ==============================
    // 🔹 Métodos de CUSTOMER, SELLER e ADMIN
    // ==============================

    // Retorna um usuário pelo username
    public User findByUsername(String username) {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new UserNotFoundException("Usuário não encontrado: " + username));
    }

    // Atualiza os dados do próprio usuário autenticado
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

    // Exclui a própria conta do usuário autenticado
    public void deleteUserByUsername(String username, User authenticatedUser) {
        if (!authenticatedUser.getUsername().equals(username)) {
            throw new AccessDeniedException("Usuário só pode excluir a própria conta.");
        }

        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UserNotFoundException("Usuário não encontrado: " + username));

        userRepository.delete(user);
    }

    // ==============================
    // 🔹 Métodos restritos ao ADMIN
    // ==============================

    // Retorna a lista de todos os usuários cadastrados (Apenas ADMIN)
    public List<UserDTO> findAll(User authenticatedUser) {
        if (authenticatedUser.getRole() != Role.ADMIN) {
            throw new AccessDeniedException("Apenas administradores podem listar todos os usuários.");
        }

        return userRepository.findAll().stream()
                .map(UserDTO::new)
                .collect(Collectors.toList());
    }

    // Retorna usuário por ID (Apenas ADMIN)
    public User findById(Long id, User authenticatedUser) {
        if (authenticatedUser.getRole() != Role.ADMIN) {
            throw new AccessDeniedException("Apenas administradores podem buscar usuários pelo ID.");
        }

        return userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException("Usuário não encontrado com ID: " + id));
    }

    // Deleta qualquer usuário por ID (Apenas ADMIN)
    public void deleteUserById(Long id, User authenticatedUser) {
        if (authenticatedUser.getRole() != Role.ADMIN) {
            throw new AccessDeniedException("Apenas administradores podem excluir usuários.");
        }
        if (!userRepository.existsById(id)) {
            throw new UserNotFoundException("Usuário não encontrado com ID: " + id);
        }
        userRepository.deleteById(id);
    }
}
