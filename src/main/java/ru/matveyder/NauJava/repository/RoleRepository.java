package ru.matveyder.NauJava.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.matveyder.NauJava.entity.Role;

import java.util.Optional;

/**
 * Репозиторий для работы с сущностью Role.
 */
public interface RoleRepository extends JpaRepository<Role, Long> {
    Optional<Role> findByTitle(String title);
}