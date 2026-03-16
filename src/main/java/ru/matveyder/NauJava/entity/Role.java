package ru.matveyder.NauJava.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.util.HashSet;
import java.util.Set;

/**
 * Класс-сущность, представляющий роль пользователя.
 * Используется для разграничения прав доступа в чате.
 */
@Entity
@Table(name = "roles")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Role {

    /// Уникальный идентификатор роли.
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /// Название роли (например, ADMIN, USER).
    @Column(nullable = false, unique = true)
    private String title;

    /// Описание роли.
    @Column
    private String description;

    /// Уровень доступа (чем больше, тем выше права).
    @Column
    private Integer level;

    /// Пользователи, имеющие эту роль.
    @OneToMany(mappedBy = "role")
    private Set<User> users = new HashSet<>();
}