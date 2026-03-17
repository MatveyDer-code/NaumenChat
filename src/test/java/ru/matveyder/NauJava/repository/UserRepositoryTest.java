package ru.matveyder.NauJava.repository;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase;
import ru.matveyder.NauJava.entity.Role;
import ru.matveyder.NauJava.entity.User;

import java.util.List;
import java.util.Optional;

/**
 * Автотесты для UserRepository.
 */
@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class UserRepositoryTest {

    /// Репозиторий для работы с пользователями.
    private final UserRepository userRepository;
    /// Репозиторий для работы с ролями.
    private final RoleRepository roleRepository;

    /// Конструктор с внедрением зависимостей.
    @Autowired
    UserRepositoryTest(UserRepository userRepository, RoleRepository roleRepository) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
    }

    /// Тест findByUsername: поиск пользователя по логину.
    @Test
    void testFindByUsername() {
        Role role = getOrCreateUserRole();
        User user = new User();
        user.setUsername("test_user_" + System.currentTimeMillis());
        user.setPassword("pass");
        user.setEmail("test@example.com");
        user.setRole(role);
        userRepository.save(user);

        Optional<User> found = userRepository.findByUsername(user.getUsername());

        Assertions.assertTrue(found.isPresent());
        Assertions.assertEquals(user.getEmail(), found.get().getEmail());
    }

    /// Тест findByUsernameAndEmail: поиск по имени и email (ключевое слово And).
    @Test
    void testFindByUsernameAndEmail() {
        Role role = getOrCreateUserRole();
        User user = new User();
        user.setUsername("and_test_" + System.currentTimeMillis());
        user.setPassword("pass");
        user.setEmail("and@test.com");
        user.setRole(role);
        userRepository.save(user);

        List<User> found = userRepository.findByUsernameAndEmail(user.getUsername(), user.getEmail());

        Assertions.assertFalse(found.isEmpty());
        Assertions.assertEquals(user.getUsername(), found.getFirst().getUsername());
    }

    /// Тест findByRoleTitle: поиск пользователей по названию роли (через @Query + JPQL).
    @Test
    void testFindByRoleTitle_JPQL() {
        Role role = new Role();
        role.setTitle("MODERATOR_" + System.currentTimeMillis());
        role.setDescription("Модератор");
        role.setLevel(50);
        roleRepository.save(role);

        User user1 = new User();
        user1.setUsername("mod_user_1");
        user1.setPassword("pass");
        user1.setRole(role);
        userRepository.save(user1);

        User user2 = new User();
        user2.setUsername("mod_user_2");
        user2.setPassword("pass");
        user2.setRole(role);
        userRepository.save(user2);

        List<User> found = userRepository.findByRoleTitle(role.getTitle());

        Assertions.assertFalse(found.isEmpty());
        Assertions.assertEquals(2, found.size());
    }

    // === Вспомогательные методы ===

    /// Получить роль "USER" или создать её, если не существует.
    private Role getOrCreateUserRole() {
        return roleRepository.findByTitle("USER")
                .orElseGet(() -> {
                    Role role = new Role();
                    role.setTitle("USER");
                    role.setDescription("Обычный пользователь");
                    role.setLevel(1);
                    return roleRepository.save(role);
                });
    }
}