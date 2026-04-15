package ru.matveyder.NauJava.service;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.security.crypto.password.PasswordEncoder;
import ru.matveyder.NauJava.entity.Role;
import ru.matveyder.NauJava.entity.User;
import ru.matveyder.NauJava.repository.RoleRepository;
import ru.matveyder.NauJava.repository.UserRepository;

import java.util.List;
import java.util.Optional;

/**
 * Unit-тесты для UserService с использованием Mockito.
 * Проверяет бизнес-логику без обращения к реальной БД.
 */
class UserServiceTest {

    /// Мок репозитория пользователей.
    private UserRepository userRepository;
    /// Мок репозитория ролей.
    private RoleRepository roleRepository;
    /// Мок энкодера паролей.
    private PasswordEncoder passwordEncoder;
    /// Тестируемый сервис.
    private UserService userService;

    /// Инициализация моков перед каждым тестом.
    @BeforeEach
    void setUp() {
        userRepository = Mockito.mock(UserRepository.class);
        roleRepository = Mockito.mock(RoleRepository.class);
        passwordEncoder = Mockito.mock(PasswordEncoder.class);
        userService = new UserService(userRepository, roleRepository, passwordEncoder);
    }

    /// Позитивный сценарий: успешная регистрация нового пользователя.
    @Test
    void testRegisterUser_Success() {
        Role role = new Role();
        role.setTitle("USER");

        Mockito.when(userRepository.findByUsername("ivan")).thenReturn(Optional.empty());
        Mockito.when(roleRepository.findByTitle("USER")).thenReturn(Optional.of(role));
        Mockito.when(passwordEncoder.encode("pass123")).thenReturn("hashed_pass");

        userService.registerUser("ivan", "pass123", "ivan@test.com");

        Mockito.verify(userRepository).save(Mockito.any(User.class));
    }

    /// Негативный сценарий: регистрация с уже существующим логином → исключение.
    @Test
    void testRegisterUser_DuplicateUsername_ThrowsException() {
        User existing = new User();
        existing.setUsername("ivan");

        Mockito.when(userRepository.findByUsername("ivan")).thenReturn(Optional.of(existing));

        Assertions.assertThrows(IllegalArgumentException.class, () ->
                userService.registerUser("ivan", "pass", "ivan@test.com")
        );

        Mockito.verify(userRepository, Mockito.never()).save(Mockito.any());
    }

    /// Позитивный сценарий: получение пользователя по существующему ID.
    @Test
    void testGetUserById_Found() {
        User user = new User();
        user.setUsername("petr");

        Mockito.when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        Optional<User> result = userService.getUserById(1L);

        Assertions.assertTrue(result.isPresent());
        Assertions.assertEquals("petr", result.get().getUsername());
    }

    /// Негативный сценарий: пользователь по ID не найден.
    @Test
    void testGetUserById_NotFound() {
        Mockito.when(userRepository.findById(99L)).thenReturn(Optional.empty());

        Optional<User> result = userService.getUserById(99L);

        Assertions.assertFalse(result.isPresent());
    }

    /// Позитивный сценарий: удаление пользователя.
    @Test
    void testDeleteUser() {
        userService.deleteUser(1L);

        Mockito.verify(userRepository).deleteById(1L);
    }

    /// Позитивный сценарий: смена пароля существующего пользователя.
    @Test
    void testChangePassword_Success() {
        User user = new User();
        user.setUsername("ivan");
        user.setPassword("old_pass");

        Mockito.when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        userService.changePassword(1L, "new_pass");

        Mockito.verify(userRepository).save(user);
        Assertions.assertEquals("new_pass", user.getPassword());
    }

    /// Негативный сценарий: смена пароля несуществующего пользователя → исключение.
    @Test
    void testChangePassword_UserNotFound_ThrowsException() {
        Mockito.when(userRepository.findById(99L)).thenReturn(Optional.empty());

        Assertions.assertThrows(IllegalArgumentException.class, () ->
                userService.changePassword(99L, "new_pass")
        );
    }

    /// Позитивный сценарий: поиск пользователя по логину.
    @Test
    void testFindByLogin_Found() {
        User user = new User();
        user.setUsername("anna");

        Mockito.when(userRepository.findByUsername("anna")).thenReturn(Optional.of(user));

        Optional<User> result = userService.findByLogin("anna");

        Assertions.assertTrue(result.isPresent());
    }

    /// Негативный сценарий: пользователь по логину не найден.
    @Test
    void testFindByLogin_NotFound() {
        Mockito.when(userRepository.findByUsername("ghost")).thenReturn(Optional.empty());

        Optional<User> result = userService.findByLogin("ghost");

        Assertions.assertFalse(result.isPresent());
    }

    /// Позитивный сценарий: получение всех пользователей.
    @Test
    void testGetAllUsers() {
        User u1 = new User();
        User u2 = new User();

        Mockito.when(userRepository.findAll()).thenReturn(List.of(u1, u2));

        List<User> result = userService.getAllUsers();

        Assertions.assertEquals(2, result.size());
    }
}