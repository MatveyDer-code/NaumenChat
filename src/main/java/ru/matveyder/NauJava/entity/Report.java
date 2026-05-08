package ru.matveyder.NauJava.entity;

import jakarta.persistence.*;
import lombok.*;
import ru.matveyder.NauJava.entity.utility.ReportStatus;

/**
 * Сущность, представляющая отчет.
 * Используется для хранения сгенерированных отчетов в базе данных.
 */
@Entity
@Table(name = "reports")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
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
    @EqualsAndHashCode.Include
    private String content;
}