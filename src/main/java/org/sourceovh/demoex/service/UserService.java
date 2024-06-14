package org.sourceovh.demoex.service;

import jakarta.transaction.Transactional;
import org.sourceovh.demoex.model.Role;
import org.sourceovh.demoex.model.User;
import org.sourceovh.demoex.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.security.Principal;
import java.util.List;
import java.util.Optional;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public Optional<User> getByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    @Transactional
    public boolean createUser(User user) {
        String email = user.getEmail();
        String phoneNumber = user.getNumberPhone();

        if (userRepository.findByEmail(email) != null) {
            throw new IllegalArgumentException("Пользователь с email: " + email + " уже существует!");
        }

        if (userRepository.findByNumberPhone(phoneNumber) != null) {
            throw new IllegalArgumentException("Пользователь с номером телефона: " + phoneNumber + " уже существует!");
        }

        user.setPassword(passwordEncoder.encode(user.getPassword()));

        user.setActive(true);
        user.getRoles().add(Role.ROLE_USER);

        userRepository.save(user);
        return true;
    }
    public User getUserByPrincipal(Principal principal) {
        if (principal == null) return new User();
        return userRepository.findByEmail(principal.getName());
    }
    public boolean existsByEmailOrNumberPhone(String email, String numberPhone) {
        return userRepository.existsByEmailOrNumberPhone(email, numberPhone);
    }

    public User save(User user) {
        return userRepository.save(user);
    }

    public void deleteUser(Long userId) {
        userRepository.deleteById(userId);
    }
    public Optional<User> findById(Long id) {
        return userRepository.findById(id);
    }
    public List<User> findAll() {
        return userRepository.findAll();
    }
}
