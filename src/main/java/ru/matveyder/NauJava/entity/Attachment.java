package ru.matveyder.NauJava.entity;

import jakarta.persistence.*;
import lombok.*;
import jakarta.validation.constraints.NotNull;

/**
 * Класс-сущность, представляющий вложение к сообщению.
 * Используется для хранения информации о файлах.
 */
@Entity
@Table(name = "attachments")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class Attachment {

    /// Уникальный идентификатор вложения.
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /// Путь к файлу на сервере.
    @NotNull
    @Column
    private String filePath;

    /// Размер файла в байтах.
    @Column
    private Integer fileSize;

    /// MIME-тип файла (например, image/png).
    @Column
    private String mimeType;

    /// Сообщение, к которому прикреплено вложение.
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "message_id", nullable = false)
    @EqualsAndHashCode.Include
    private Message message;
}