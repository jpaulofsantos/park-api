package com.jp.parkapi.services;

import com.jp.parkapi.entities.User;
import com.jp.parkapi.exception.EntityNotFoundException;
import com.jp.parkapi.exception.PasswordInvalidException;
import com.jp.parkapi.exception.UserNameUniqueViolationException;
import com.jp.parkapi.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Transactional
    public User insertUser(User user) {
        try {
            user.setPassword(passwordEncoder.encode(user.getPassword()));
            return userRepository.save(user);
        } catch (DataIntegrityViolationException e) {
            throw new UserNameUniqueViolationException(String.format("Username {%s} ja cadastrado", user.getUsername()));
        }
    }

    @Transactional(readOnly = true)
    public User findById(Long id) {
        return userRepository.findById(id).orElseThrow(
                () -> new EntityNotFoundException(String.format("Usuário id '%s' não encontrado.", id)));
    }

    @Transactional(readOnly = true)
    public User updatePassword(Long id, String senhaAtual, String novaSenha, String confirmaSenha) {
        if (!novaSenha.equals(confirmaSenha)) {
            throw new PasswordInvalidException(String.format("Nova senha é diferente da confirmação da senha"));
        }

        User user = findById(id);
        if (!passwordEncoder.matches(senhaAtual, user.getPassword())) {
            throw new PasswordInvalidException(String.format("Senha atual informada é inválida"));
        }
        user.setPassword(passwordEncoder.encode(novaSenha));
        userRepository.save(user);
        return user;
    }

    @Transactional(readOnly = true)
    public Page<User> findAllUsersPaged(Pageable pageable) {
        return userRepository.findAll(pageable);
    }

    @Transactional(readOnly = true)
    public User findByUsername(String username) {
        return userRepository.findByUsername(username).orElseThrow(
                () -> new EntityNotFoundException(String.format("Username {%s} não encontrado.", username)));
    }

    @Transactional(readOnly = true)
    public User.Role findRoleByUsername(String username) {
        return userRepository.findRoleByUsername(username);
    }
}
