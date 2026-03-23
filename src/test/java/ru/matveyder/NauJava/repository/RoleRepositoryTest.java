package ru.matveyder.NauJava.repository;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase;
import ru.matveyder.NauJava.entity.Role;

import java.util.Optional;

/**
 * Автотесты для RoleRepository.
 */
@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class RoleRepositoryTest {

    /// Репозиторий для работы с ролями.
    private final RoleRepository roleRepository;

    /// Конструктор с внедрением зависимости.
    @Autowired
    RoleRepositoryTest(RoleRepository roleRepository) {
        this.roleRepository = roleRepository;
    }

    /// Тест метода findByTitle: поиск существующей роли.
    @Test
    void testFindByTitle_Exists() {
        // Подготовка: создаём роль
        Role role = new Role();
        role.setTitle("ADMIN");
        role.setDescription("Администратор");
        role.setLevel(100);
        roleRepository.save(role);

        /// Выполнение: поиск по названию
        Optional<Role> found = roleRepository.findByTitle("ADMIN");

        /// Проверка
        Assertions.assertTrue(found.isPresent());
        Assertions.assertEquals("Администратор", found.get().getDescription());
    }

    /// Тест метода findByTitle: роль не найдена.
    @Test
    void testFindByTitle_NotExists() {
        /// Выполнение: поиск несуществующей роли
        Optional<Role> found = roleRepository.findByTitle("NONEXISTENT");

        /// Проверка
        Assertions.assertFalse(found.isPresent());
    }
}