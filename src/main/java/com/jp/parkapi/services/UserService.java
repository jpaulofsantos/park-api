package com.jp.parkapi.services;

import com.jp.parkapi.entities.User;
import com.jp.parkapi.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Transactional
    public User insertUser(User user) {
        return userRepository.save(user);
    }

    @Transactional(readOnly = true)
    public User findById(Long id) {
        return userRepository.findById(id).orElseThrow(
                () -> new RuntimeException("Usuário não encontrado.")
        );
    }
    @Transactional
    public User updatePassword(Long id, String senhaAtual, String novaSenha, String confirmaSenha) {
        if (!novaSenha.equals(confirmaSenha)) {
            throw new RuntimeException("Nova senha é diferente da confirmação da senha");
        }

        User user = findById(id);
        if (!senhaAtual.equals(user.getPassword())) {
            throw new RuntimeException("Senha atual informada é inválida");
        }
        user.setPassword(novaSenha);
        userRepository.save(user);
        return user;
    }

    @Transactional(readOnly = true)
    public Page<User> findAllUsersPaged(Pageable pageable) {
        return userRepository.findAll(pageable);
    }
}
