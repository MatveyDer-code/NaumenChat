package ru.matveyder.NauJava.repository;

import ru.matveyder.NauJava.entity.Message;
import java.util.List;

/**
 * Интерфейс для кастомных методов поиска сущности Message.
 * Использует Criteria API для типобезопасных запросов.
 */
public interface MessageRepositoryCustom {

    /// Поиск сообщений по автору и статусу прочтения (через Criteria API).
    List<Message> findByAuthorIdAndIsReadCriteria(Long authorId, Boolean isRead);

    /// Поиск сообщений по названию комнаты (через связанную сущность, Criteria API).
    List<Message> findByChatRoomNameCriteria(String roomName);
}