package ru.matveyder.NauJava.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

/**
 * Класс-сущность, представляющий комнату чата.
 * Используется для группировки сообщений и пользователей.
 */
@Entity
@Table(name = "chat_rooms")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ChatRoom {

    /// Уникальный идентификатор комнаты.
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /// Название комнаты чата.
    @Column(nullable = false)
    private String name;

    /// Дата создания комнаты.
    @Column
    private LocalDateTime createdDate;

    /// Флаг приватности комнаты.
    @Column
    private Boolean isPrivate;

    /// Сообщения, отправленные в этой комнате.
    @OneToMany(mappedBy = "chatRoom")
    private Set<Message> messages = new HashSet<>();

    /// Пользователи, состоящие в этой комнате.
    @ManyToMany(mappedBy = "chatRooms")
    private Set<User> users = new HashSet<>();
}
