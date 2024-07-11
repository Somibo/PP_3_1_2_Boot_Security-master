package ru.kata.spring.boot_security.demo.services;

import org.springframework.security.core.userdetails.UserDetailsService;
import ru.kata.spring.boot_security.demo.models.User;

import java.util.Collection;
import java.util.List;

public interface UserService{

        List<User> findAll();
        void delete(Long id);
        void save(User user);
        User findById(Long id);
        User findByEmail(String email);
        void updateUser(User user);
    }
