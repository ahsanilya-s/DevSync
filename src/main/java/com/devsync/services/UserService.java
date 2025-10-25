package com.devsync.services;

import com.devsync.model.User;
import com.devsync.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserService {
    @Autowired
    private UserRepository userRepository;
    
    private BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    public User registerUser(User user) {
        if (userRepository.findByEmail(user.getEmail()) != null) {
            throw new RuntimeException("Email already exists");
        }
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        return userRepository.save(user);
    }

    public User login(String email, String password) {
        User existingUser = userRepository.findByEmail(email);
        if (existingUser != null && passwordEncoder.matches(password, existingUser.getPassword())) {
            return existingUser;
        }
        return null;
    }

    public java.util.List<User> getAllUsers() {
        return userRepository.findAll();
    }
}
