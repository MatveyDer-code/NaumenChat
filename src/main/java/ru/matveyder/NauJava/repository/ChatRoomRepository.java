package ru.matveyder.NauJava.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import ru.matveyder.NauJava.entity.ChatRoom;

/**
 * Репозиторий для работы с сущностью ChatRoom.
 */

@RepositoryRestResource(path = "chat-rooms")
public interface ChatRoomRepository extends JpaRepository<ChatRoom, Long> {
}