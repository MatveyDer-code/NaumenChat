package ru.matveyder.NauJava.controller;

import org.springframework.web.bind.annotation.*;
import ru.matveyder.NauJava.entity.DTO.MessageDto;
import ru.matveyder.NauJava.entity.Message;
import ru.matveyder.NauJava.repository.MessageRepository;
import ru.matveyder.NauJava.repository.MessageRepositoryCustom;

import java.util.List;

/**
 * REST контроллер для работы с кастомными методами репозитория Message.
 */
@RestController
@RequestMapping("/api/messages")
public class MessageController {

    private final MessageRepository messageRepository;
    private final MessageRepositoryCustom messageRepositoryCustom;

    public MessageController(MessageRepository messageRepository,
                             MessageRepositoryCustom messageRepositoryCustom) {
        this.messageRepository = messageRepository;
        this.messageRepositoryCustom = messageRepositoryCustom;
    }

    /// Поиск сообщений по автору и статусу прочтения (Spring Data)
    @GetMapping("/by-author")
    public List<MessageDto> findByAuthorAndIsRead(
            @RequestParam Long authorId,
            @RequestParam Boolean isRead) {
        return messageRepository.findByAuthorIdAndIsRead(authorId, isRead)
                .stream()
                .map(m -> new MessageDto(
                        m.getId(),
                        m.getContent(),
                        m.getAuthor().getId(),
                        m.getChatRoom().getId(),
                        m.getIsRead()
                ))
                .toList();
    }

    /// Поиск сообщений по названию комнаты (JPQL)
    @GetMapping("/by-room")
    public List<Message> findByChatRoomName(
            @RequestParam String roomName) {
        return messageRepository.findByChatRoomName(roomName);
    }

    /// Поиск сообщений по автору и статусу прочтения (Criteria API)
    @GetMapping("/by-author-criteria")
    public List<Message> findByAuthorAndIsReadCriteria(
            @RequestParam Long authorId,
            @RequestParam Boolean isRead) {
        return messageRepositoryCustom.findByAuthorIdAndIsReadCriteria(authorId, isRead);
    }

    /// Поиск сообщений по названию комнаты (Criteria API)
    @GetMapping("/by-room-criteria")
    public List<Message> findByChatRoomNameCriteria(
            @RequestParam String roomName) {
        return messageRepositoryCustom.findByChatRoomNameCriteria(roomName);
    }
}