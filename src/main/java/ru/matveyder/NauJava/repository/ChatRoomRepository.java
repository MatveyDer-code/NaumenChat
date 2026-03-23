package ru.matveyder.NauJava.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.matveyder.NauJava.entity.ChatRoom;

/**
 * Репозиторий для работы с сущностью ChatRoom.
 */
public interface ChatRoomRepository extends JpaRepository<ChatRoom, Long> {
}