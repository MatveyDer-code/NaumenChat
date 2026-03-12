package ru.matveyder.NauJava.entity;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

/**
 * Класс-сущность, представляющий пользователя чата.
 * Используется для хранения данных о пользователе в имитированной базе данных.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class User {
    /// Уникальный идентификатор пользователя.
    private Long id;

    /// Логин пользователя для авторизации.
    private String login;

    /// Пароль пользователя.
    private String password;
}