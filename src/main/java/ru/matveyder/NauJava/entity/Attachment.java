package ru.matveyder.NauJava.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

/**
 * Класс-сущность, представляющий вложение к сообщению.
 * Используется для хранения информации о файлах.
 */
@Entity
@Table(name = "attachments")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Attachment {

    /// Уникальный идентификатор вложения.
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /// Путь к файлу на сервере.
    @Column
    private String filePath;

    /// Размер файла в байтах.
    @Column
    private Integer fileSize;

    /// MIME-тип файла (например, image/png).
    @Column
    private String mimeType;

    /// Сообщение, к которому прикреплено вложение.
    @ManyToOne
    @JoinColumn(name = "message_id", nullable = false)
    private Message message;
}