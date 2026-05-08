package ru.matveyder.NauJava.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.HashSet;
import java.util.Set;

/**
 * Класс-сущность, представляющий роль пользователя.
 * Используется для разграничения прав доступа в чате.
 */
@Entity
@Table(name = "roles")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class Role {

    /// Уникальный идентификатор роли.
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @EqualsAndHashCode.Include
    private Long id;

    /// Название роли (например, ADMIN, USER).
    @EqualsAndHashCode.Include
    @Column(nullable = false, unique = true)
    private String title;

    /// Описание роли.
    @Column
    private String description;

    /// Уровень доступа (чем больше, тем выше права).
    @Column
    private Integer level;
}