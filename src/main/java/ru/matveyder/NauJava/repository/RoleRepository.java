package ru.matveyder.NauJava.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import ru.matveyder.NauJava.entity.Role;

import java.util.Optional;

/**
 * Репозиторий для работы с сущностью Role.
 */

@RepositoryRestResource(path = "roles")
public interface RoleRepository extends JpaRepository<Role, Long> {
    Optional<Role> findByTitle(String title);
}