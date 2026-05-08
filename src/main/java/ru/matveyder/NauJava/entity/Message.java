package ru.matveyder.NauJava.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

/**
 * Класс-сущность, представляющий сообщение в чате.
 * Используется для хранения текста и метаданных сообщения.
 */
@Entity
@Table(name = "messages")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class Message {

    /// Уникальный идентификатор сообщения.
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /// Текст сообщения.
    @Column(nullable = false, columnDefinition = "TEXT")
    @EqualsAndHashCode.Include
    private String content;

    /// Дата и время отправки сообщения.
    @Column
    private LocalDateTime sendDate;

    /// Флаг прочтения сообщения.
    @Column
    private Boolean isRead;

    /// Автор сообщения.
    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User author;

    /// Комната, в которую отправлено сообщение.
    @ManyToOne
    @JoinColumn(name = "chat_room_id", nullable = false)
    private ChatRoom chatRoom;

    /// Вложения, прикреплённые к сообщению.
    @OneToMany(mappedBy = "message")
    private Set<Attachment> attachments = new HashSet<>();
}