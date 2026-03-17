package ru.matveyder.NauJava.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

/**
 * Класс-сущность, представляющий пользователя чата.
 * Используется для хранения данных о пользователе в имитированной базе данных.
 */
@Entity
@Table(name = "users")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class User {
    /// Уникальный идентификатор пользователя.
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;


    /// Логин пользователя для авторизации.
    @Column(nullable = false, unique = true)
    private String username;

    /// Пароль пользователя (в реальном проекте нужно хешировать).
    @Column(nullable = false)
    private String password;

    /// Email пользователя.
    @Column
    private String email;

    /// Дата последнего входа пользователя.
    @Column
    private LocalDateTime lastLoginDate;

    /// Роль пользователя.
    @ManyToOne
    @JoinColumn(name = "role_id", nullable = false)
    private Role role;

    /// Сообщения, написанные пользователем.
    @OneToMany(mappedBy = "author")
    private Set<Message> messages = new HashSet<>();

    /// Комнаты чата, в которых состоит пользователь.
    @ManyToMany
    @JoinTable(
            name = "user_chat_room",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "chat_room_id")
    )
    private Set<ChatRoom> chatRooms = new HashSet<>();
}