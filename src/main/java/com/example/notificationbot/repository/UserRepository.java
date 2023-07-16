package com.example.notificationbot.repository;

import com.example.notificationbot.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String email);
    Optional<User> findByCode(Integer code);

    Optional<User> findByTelegramUserId(Long id);
}
