package ru.matveyder.NauJava.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.matveyder.NauJava.entity.Message;
import java.util.List;

/**
 * Репозиторий для работы с сущностью Message.
 */
public interface MessageRepository extends JpaRepository<Message, Long> {

    /// Поиск сообщений по автору и статусу прочтения (ключевое слово And).
    List<Message> findByAuthorIdAndIsRead(Long authorId, Boolean isRead);

    /// Поиск сообщений по названию комнаты (через связанную сущность, JPQL).
    @Query("SELECT m FROM Message m WHERE m.chatRoom.name = :roomName")
    List<Message> findByChatRoomName(@Param("roomName") String roomName);

    @Query("SELECT CASE WHEN COUNT(m) > 0 THEN true ELSE false END FROM Message m WHERE m.content = :prefix")
    boolean existsByContentPrefix(@Param("prefix") String prefix);
}