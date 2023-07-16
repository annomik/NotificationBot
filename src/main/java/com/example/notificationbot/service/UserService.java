package com.example.notificationbot.service;

import com.example.notificationbot.model.User;
import com.example.notificationbot.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
@Service
@Transactional
public class UserService {

    private final UserRepository userRepository;

    private final EmailService emailService;

    @Transactional(readOnly = true)
    public List<User> findAll() {
        return userRepository.findAll();
    }

    @Transactional(readOnly = true)
    public Optional<User> findById(Long id) {
        return userRepository.findById(id);
    }

    @Transactional(readOnly = true)
    public Optional<User> findByEmail(String email) {
        return userRepository.findByEmail(email);
    }


    @Transactional(readOnly = true)
    public Optional<User> findByTelegramUserId(Long id) {
        return userRepository.findByTelegramUserId(id);
    }


    public void save(User user) {
        int verificationCode =(int) (Math.random() * 10000);
        user.setCode(verificationCode);
        userRepository.save(user);
        String message = String.format("Hello, %s! \n" +
                "To successfully activate your account, please, visit link: http://localhost:8080/users/activate/%s",
                user.getName(),
                user.getCode()
        );
        emailService.send(user.getEmail(), "Activation", message);
    }

    public boolean activateUser(Integer code) {
        Optional<User> user = userRepository.findByCode(code);
        return user.isPresent();
    }

    public void update(Long id, User user) {
        user.setId(id);
        userRepository.save(user);
    }

    public void delete(Long id) {
        userRepository.deleteById(id);
    }
}
