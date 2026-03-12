package ru.matveyder.NauJava.service;

import ru.matveyder.NauJava.entity.User;
import java.util.List;

/**
 * Интерфейс сервиса для управления пользователями.
 * Описывает бизнес-логику приложения: регистрацию, поиск, удаление и изменение данных пользователей.
 */
public interface UserService {

    /**
     * Регистрирует нового пользователя в системе.
     * @param id уникальный идентификатор пользователя
     * @param login логин пользователя
     * @param password пароль пользователя
     */
    void registerUser(Long id, String login, String password);

    /**
     * Находит пользователя по его уникальному идентификатору.
     * @param id идентификатор пользователя
     * @return объект пользователя или null, если пользователь не найден
     */
    User getUserById(Long id);

    /**
     * Удаляет пользователя из системы по его идентификатору.
     * @param id идентификатор пользователя для удаления
     */
    void deleteUser(Long id);

    /**
     * Возвращает список всех зарегистрированных пользователей.
     * @return список объектов User
     */
    List<User> getAllUsers();

    /**
     * Изменяет пароль существующего пользователя.
     * @param id идентификатор пользователя
     * @param newPassword новый пароль
     */
    void changePassword(Long id, String newPassword);
}