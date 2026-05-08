package ru.matveyder.NauJava.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

/**
 * Класс-сущность, представляющий пользователя чата.
 * Используется для хранения данных о пользователе в имитированной базе данных.
 */
@Entity
@Table(name = "users")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class User {
    /// Уникальный идентификатор пользователя.
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;


    /// Логин пользователя для авторизации.
    @EqualsAndHashCode.Include
    @Column(nullable = false, unique = true)
    private String username;

    /// Пароль пользователя
    @Column(nullable = false)
    private String password;

    /// Email пользователя.
    @Column(nullable = false, unique = true)
    private String email;

    /// Дата последнего входа пользователя.
    @Column
    private LocalDateTime lastLoginDate;

    /// Роль пользователя.
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "role_id", nullable = false)
    private Role role;

    /// Комнаты чата, в которых состоит пользователь.
    @ManyToMany
    @JoinTable(
            name = "user_chat_room",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "chat_room_id")
    )
    private Set<ChatRoom> chatRooms = new HashSet<>();

    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", username='" + username + '\'' +
                ", email='" + email + '\'' +
                ", role='" + (role != null ? role.getTitle() : "null") + '\'' +
                '}';
    }
}