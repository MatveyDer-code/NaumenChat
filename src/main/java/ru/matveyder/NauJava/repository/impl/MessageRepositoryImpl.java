package ru.matveyder.NauJava.repository.impl;

import jakarta.persistence.EntityManager;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.JoinType;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import ru.matveyder.NauJava.entity.ChatRoom;
import ru.matveyder.NauJava.entity.Message;
import ru.matveyder.NauJava.repository.MessageRepositoryCustom;

import java.util.List;

/**
 * Реализация кастомных методов поиска для Message через Criteria API.
 */
@Repository
public class MessageRepositoryImpl implements MessageRepositoryCustom {

    private final EntityManager entityManager;

    @Autowired
    public MessageRepositoryImpl(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    @Override
    public List<Message> findByAuthorIdAndIsRead(Long authorId, Boolean isRead) {
        /// Получаем билдер запросов
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        /// Создаем запрос для сущности Message
        CriteriaQuery<Message> cq = cb.createQuery(Message.class);
        /// Определяем корневой элемент (FROM Message)
        Root<Message> message = cq.from(Message.class);

        /// Создаем условия: author.id = ? AND isRead = ?
        Predicate authorPredicate = cb.equal(message.get("author").get("id"), authorId);
        Predicate readPredicate = cb.equal(message.get("isRead"), isRead);

        /// Формируем запрос: SELECT * FROM Message WHERE author.id = ? AND isRead = ?
        cq.select(message).where(cb.and(authorPredicate, readPredicate));

        /// Выполняем запрос
        return entityManager.createQuery(cq).getResultList();
    }

    @Override
    public List<Message> findByChatRoomName(String roomName) {
        /// Получаем билдер запросов
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        /// Создаем запрос для сущности Message
        CriteriaQuery<Message> cq = cb.createQuery(Message.class);
        /// Определяем корневой элемент (FROM Message)
        Root<Message> message = cq.from(Message.class);

        /// Делаем JOIN с ChatRoom: JOIN message.chatRoom
        Join<Message, ChatRoom> chatRoom = message.join("chatRoom", JoinType.INNER);

        /// Создаем условие: chatRoom.name = ?
        Predicate namePredicate = cb.equal(chatRoom.get("name"), roomName);

        /// Формируем запрос: SELECT * FROM Message m JOIN m.chatRoom c WHERE c.name = ?
        cq.select(message).where(namePredicate);

        /// Выполняем запрос
        return entityManager.createQuery(cq).getResultList();
    }
}