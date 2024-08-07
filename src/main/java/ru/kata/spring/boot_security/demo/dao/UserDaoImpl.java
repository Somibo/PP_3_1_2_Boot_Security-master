package ru.kata.spring.boot_security.demo.dao;

import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import ru.kata.spring.boot_security.demo.models.User;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import java.util.List;
import java.util.Optional;

@Repository
public class UserDaoImpl implements UserDao {

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public Optional<User> findByEmail(String email) {
        TypedQuery<User> query = entityManager.createQuery(
                "SELECT u FROM User u LEFT JOIN FETCH u.roles WHERE u.email = :email", User.class);
        query.setParameter("email", email);
        List<User> users = query.getResultList();
        if (users.isEmpty()) {
            return Optional.empty();
        }
        return Optional.of(users.get(0));
    }

    @Override
    public List<User> findAll() {
        TypedQuery<User> query = entityManager.createQuery("SELECT u FROM User u", User.class);
        return query.getResultList();
    }

    @Override
    @Transactional
    public void deleteById(Long id) {
        User user = entityManager.find(User.class, id);
        if (user != null) {
            entityManager.remove(user);
        }
    }

    @Override
    @Transactional
    public void save(User user) {
        if (user.getId() != null) {
            // Проверяем, существует ли пользователь с указанным id
            User existingUser = entityManager.find(User.class, user.getId());
            if (existingUser != null) {
                throw new IllegalArgumentException("User with ID " + user.getId() + " already exists.");
            }
        }
        entityManager.persist(user); // если пользователь не существует
    }

    @Override
    @Transactional
    public void update(User user) {
        // Проверяем, существует ли пользователь с указанным id
        User existingUser = entityManager.find(User.class, user.getId());
        if (existingUser == null) {
            throw new IllegalArgumentException("User with ID " + user.getId() + " not found.");
        }

        entityManager.merge(user); // обновляем пользователя
    }

    @Override
    public Optional<User> findById(Long id) {
        return Optional.ofNullable(entityManager.find(User.class, id));
    }
}
