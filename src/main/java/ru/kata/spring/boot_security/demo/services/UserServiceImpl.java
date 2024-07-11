package ru.kata.spring.boot_security.demo.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.kata.spring.boot_security.demo.dao.RoleDao;
import ru.kata.spring.boot_security.demo.dao.UserDao;
import ru.kata.spring.boot_security.demo.models.Role;
import ru.kata.spring.boot_security.demo.models.User;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;

@Service
@Transactional
public class UserServiceImpl implements UserService {

    private final UserDao userDao;
    private final RoleDao roleDao;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;

    @Autowired
    public UserServiceImpl(UserDao userDao, RoleDao roleDao, BCryptPasswordEncoder bCryptPasswordEncoder) {
        this.userDao = userDao;
        this.roleDao = roleDao;
        this.bCryptPasswordEncoder = bCryptPasswordEncoder;
    }

    @Override
    public List<User> findAll() {
        return userDao.findAll();
    }

    @Override
    public void delete(Long id) {
        userDao.deleteById(id);
    }

    @Override
    public void save(User user) {
        List<Role> roles = new ArrayList<>();
        Role userRole = roleDao.findAll().stream()
                .filter(role -> role.getRoleName().equals("ROLE_USER"))
                .findFirst()
                .orElseThrow(() -> new IllegalStateException("Role USER not found"));
        roles.add(userRole);

        if (user.isAdmin()) {
            Role adminRole = roleDao.findAll().stream()
                    .filter(role -> role.getRoleName().equals("ROLE_ADMIN"))
                    .findFirst()
                    .orElseThrow(() -> new IllegalStateException("Role ADMIN not found"));
            roles.add(adminRole);
        }

        user.setRoles(roles);
        user.setPassword(bCryptPasswordEncoder.encode(user.getPassword()));
        userDao.save(user);
    }

    @Override
    public User findById(Long id) {
        return userDao.findById(id).orElse(null);
    }

    @Override
    public User findByEmail(String email) {
        return userDao.findByEmail(email)
                .orElseThrow(() -> new NoSuchElementException("User not found"));
    }

    @Override
    public void updateUser(User user) {
        // Проверяем, существует ли пользователь с указанным id
        User existingUser = userDao.findById(user.getId())
                .orElseThrow(() -> new IllegalArgumentException("User with ID " + user.getId() + " not found."));

        // Обновляем информацию о пользователе
        existingUser.setFirstName(user.getFirstName());
        existingUser.setLastName(user.getLastName());
        existingUser.setEmail(user.getEmail());

        // Если нужно обновить пароль, кодирование можно сделать также.
        if (user.getPassword() != null) {
            existingUser.setPassword(bCryptPasswordEncoder.encode(user.getPassword()));
        }

        // Если нужно обновить роли, также можно сделать, но для примера, этого нет.
        userDao.update(existingUser);
    }
}
