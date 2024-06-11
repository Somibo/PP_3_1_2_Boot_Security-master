package ru.kata.spring.boot_security.demo.DAO;

import ru.kata.spring.boot_security.demo.models.User;

import java.util.List;
import java.util.Optional;

public interface UserDao {
    Optional<User> findByEmail(String email);
    List<User> findAll();
    void deleteById(Long id);
    void save(User user);
    Optional<User> findById(Long id);
}
