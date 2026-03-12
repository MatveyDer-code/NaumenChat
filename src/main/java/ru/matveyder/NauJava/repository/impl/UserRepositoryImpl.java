package ru.matveyder.NauJava.repository.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import ru.matveyder.NauJava.entity.User;
import ru.matveyder.NauJava.repository.UserRepository;
import java.util.List;
import java.util.Optional;

/**
 * Реализация интерфейса UserRepository.
 * Использует список в памяти как имитацию базы данных.
 * Класс является Singleton-бином Spring.
 */
@Repository
public class UserRepositoryImpl implements UserRepository {

    private final List<User> database;

    /**
     * Конструктор с внедрением зависимости списка пользователей через Spring.
     * @param database список, выступающий в роли хранилища данных
     */
    @Autowired
    public UserRepositoryImpl(List<User> database) {
        this.database = database;
    }

    @Override
    public void create(User user) {
        database.add(user);
    }

    @Override
    public User read(Long id) {
        return database.stream()
                .filter(u -> u.getId().equals(id))
                .findFirst()
                .orElse(null);
    }

    @Override
    public void update(User user) {
        Optional<User> existing = database.stream()
                .filter(u -> u.getId().equals(user.getId()))
                .findFirst();
        if (existing.isPresent()) {
            User u = existing.get();
            u.setLogin(user.getLogin());
            u.setPassword(user.getPassword());
        }
    }

    @Override
    public void delete(Long id) {
        database.removeIf(u -> u.getId().equals(id));
    }

    @Override
    public List<User> findAll() {
        return database;
    }
}