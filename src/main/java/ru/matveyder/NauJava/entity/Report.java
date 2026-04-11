package ru.matveyder.NauJava.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.matveyder.NauJava.entity.utility.ReportStatus;

/**
 * Сущность, представляющая отчет.
 * Используется для хранения сгенерированных отчетов в базе данных.
 */
@Entity
@Table(name = "reports")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Report {

    /// Уникальный идентификатор отчета.
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /// Статус формирования отчета (создан, завершен, ошибка).
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ReportStatus status;

    /// Содержимое отчета в формате HTML.
    @Lob
    @Column(nullable = false)
    private String content;
}