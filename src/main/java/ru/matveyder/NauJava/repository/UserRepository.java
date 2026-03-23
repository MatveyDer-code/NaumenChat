package ru.matveyder.NauJava.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.matveyder.NauJava.entity.User;
import java.util.List;
import java.util.Optional;

/**
 * Репозиторий для работы с сущностью User.
 */
public interface UserRepository extends JpaRepository<User, Long> {

    /// Поиск пользователя по логину (Query Lookup Strategy).
    Optional<User> findByUsername(String username);

    /// Поиск пользователей по имени и email (использует ключевое слово And).
    List<User> findByUsernameAndEmail(String username, String email);

    /// Поиск пользователей по названию роли (через связанную сущность, JPQL).
    @Query("SELECT u FROM User u WHERE u.role.title = :roleTitle")
    List<User> findByRoleTitle(@Param("roleTitle") String roleTitle);
}