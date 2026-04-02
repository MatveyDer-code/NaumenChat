package ru.matveyder.NauJava.service;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import ru.matveyder.NauJava.entity.Role;
import ru.matveyder.NauJava.entity.User;
import ru.matveyder.NauJava.repository.RoleRepository;
import ru.matveyder.NauJava.repository.UserRepository;
import java.util.List;
import java.util.Optional;

/**
 * Сервис для управления пользователями.
 * Работает с базой данных через UserRepository.
 */
@Service
public class UserService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;

    /// Конструктор с внедрением зависимости
    public UserService(UserRepository userRepository, RoleRepository roleRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
    }

    /// Регистрирует нового пользователя
    public void registerUser(String login, String password, String email) {
        if (userRepository.findByUsername(login).isPresent()) {
            throw new IllegalArgumentException("Пользователь с логином '" + login + "' уже существует");
        }

        /// Находим или создаём роль "USER" по умолчанию
        Role userRole = roleRepository.findByTitle("USER")
                .orElseGet(() -> {
                    Role role = new Role();
                    role.setTitle("USER");
                    role.setDescription("Обычный пользователь");
                    role.setLevel(1);
                    return roleRepository.save(role);
                });

        User user = new User();
        user.setUsername(login);
        user.setPassword(passwordEncoder.encode(password));
        user.setEmail(email);
        user.setRole(userRole);
        userRepository.save(user);
    }

    /// Находит пользователя по ID
    public Optional<User> getUserById(Long id) {
        return userRepository.findById(id);
    }

    /// Удаляет пользователя
    public void deleteUser(Long id) {
        userRepository.deleteById(id);
    }

    /// Возвращает всех пользователей
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    /// Меняет пароль
    public void changePassword(Long id, String newPassword) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Пользователь с ID " + id + " не найден"));
        user.setPassword(newPassword);
        userRepository.save(user);
    }

    /// Поиск по логину (для авторизации)
    public Optional<User> findByLogin(String login) {
        return userRepository.findByUsername(login).stream().findFirst();
    }
}