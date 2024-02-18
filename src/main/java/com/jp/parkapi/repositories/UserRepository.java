package com.jp.parkapi.repositories;

import com.jp.parkapi.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByUsername(String username);

    @Query("SELECT user.Role from User user WHERE user.username LIKE :username")
    User.Role findRoleByUsername(String username);
}
