package ru.matveyder.NauJava.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.matveyder.NauJava.entity.Attachment;
import ru.matveyder.NauJava.entity.Message;
import ru.matveyder.NauJava.repository.AttachmentRepository;
import ru.matveyder.NauJava.repository.MessageRepository;

/**
 * Сервис для операций с чатом, требующих транзакционности.
 */
@Service
public class ChatService {

    private final MessageRepository messageRepository;
    private final AttachmentRepository attachmentRepository;

    public ChatService(MessageRepository messageRepository,
                       AttachmentRepository attachmentRepository) {
        this.messageRepository = messageRepository;
        this.attachmentRepository = attachmentRepository;
    }

    /// Отправка сообщения с вложением в одной транзакции.
    /// Если сохранение вложения упадёт — сообщение тоже откатится.
    @Transactional
    public Message sendMessageWithAttachment(Message message, Attachment attachment) {
        if (attachment.getFilePath() == null || attachment.getFilePath().isEmpty()) {
            throw new IllegalArgumentException("File path cannot be null");
        }
        // 1. Сохраняем сообщение
        Message savedMessage = messageRepository.save(message);

        // 2. Привязываем его к вложению
        attachment.setMessage(savedMessage);

        // 3. Сохраняем вложение
        attachmentRepository.save(attachment);

        // 4. Возвращаем сохранённое сообщение (с прикреплённым файлом)
        return savedMessage;
    }
}