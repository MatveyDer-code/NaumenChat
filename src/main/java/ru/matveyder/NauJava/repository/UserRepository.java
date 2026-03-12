package ru.matveyder.NauJava.repository;

import ru.matveyder.NauJava.entity.User;
import java.util.List;

/**
 * Интерфейс для доступа к данным пользователя (DAO слой).
 * Описывает базовые CRUD операции для работы с сущностью User.
 */
public interface UserRepository {

    /**
     * Добавляет нового пользователя в базу данных.
     * @param user объект пользователя для добавления
     */
    void create(User user);

    /**
     * Находит пользователя по его уникальному идентификатору.
     * @param id идентификатор пользователя
     * @return объект пользователя или null, если не найден
     */
    User read(Long id);

    /**
     * Обновляет данные существующего пользователя.
     * @param user объект пользователя с обновленными данными
     */
    void update(User user);

    /**
     * Удаляет пользователя из базы данных по его идентификатору.
     * @param id идентификатор пользователя для удаления
     */
    void delete(Long id);

    /**
     * Возвращает список всех пользователей.
     * @return список всех объектов User
     */
    List<User> findAll();
}