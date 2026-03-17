package ru.matveyder.NauJava.service;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import ru.matveyder.NauJava.entity.*;
import ru.matveyder.NauJava.repository.*;

import java.time.LocalDateTime;

/**
 * Автотесты для ChatService.
 * Проверяет транзакционную операцию отправки сообщения с вложением.
 */
@SpringBootTest
@ActiveProfiles("test")
class ChatServiceTest {

    /// Сервис для операций с чатом.
    private final ChatService chatService;
    /// Репозиторий для работы с сообщениями.
    private final MessageRepository messageRepository;
    /// Репозиторий для работы с вложениями.
    private final AttachmentRepository attachmentRepository;
    /// Репозиторий для работы с пользователями.
    private final UserRepository userRepository;
    /// Репозиторий для работы с комнатами.
    private final ChatRoomRepository chatRoomRepository;
    /// Репозиторий для работы с ролями.
    private final RoleRepository roleRepository;

    /// Конструктор с внедрением зависимостей.
    @Autowired
    ChatServiceTest(ChatService chatService,
                    MessageRepository messageRepository,
                    AttachmentRepository attachmentRepository,
                    UserRepository userRepository,
                    ChatRoomRepository chatRoomRepository,
                    RoleRepository roleRepository) {
        this.chatService = chatService;
        this.messageRepository = messageRepository;
        this.attachmentRepository = attachmentRepository;
        this.userRepository = userRepository;
        this.chatRoomRepository = chatRoomRepository;
        this.roleRepository = roleRepository;
    }

    /// Тест транзакции: ошибка при сохранении → откат.
    @Test
    void testSendMessageWithAttachment_Rollback() {
        Role role = getOrCreateUserRole();
        User author = createUser("tx_rollback_user", role);
        ChatRoom room = createChatRoom("TxRollbackRoom");

        Message msg = new Message();
        msg.setContent("ShouldRollback_" + System.currentTimeMillis());
        msg.setSendDate(LocalDateTime.now());
        msg.setAuthor(author);
        msg.setChatRoom(room);

        Attachment attach = new Attachment();
        attach.setFilePath(null);
        attach.setMessage(msg);

        Assertions.assertThrows(RuntimeException.class, () -> {
            chatService.sendMessageWithAttachment(msg, attach);
        });
        String content = "ShouldRollback_" + System.currentTimeMillis();
        msg.setContent(content);

        boolean exists = messageRepository.existsByContentPrefix(content);
        Assertions.assertFalse(exists, "Транзакция не откатилась — сообщение осталось в БД");
    }

    /// Тест транзакции: успешная отправка сообщения с вложением.
    @Test
    void testSendMessageWithAttachment_Success() {
        Role role = getOrCreateUserRole();
        User author = createUser("tx_success_user", role);
        ChatRoom room = createChatRoom("TxSuccessRoom");

        Message msg = new Message();
        msg.setContent("Tx success message");
        msg.setSendDate(LocalDateTime.now());
        msg.setAuthor(author);
        msg.setChatRoom(room);

        Attachment attach = new Attachment();
        attach.setFilePath("/files/test.png");
        attach.setFileSize(1024);
        attach.setMimeType("image/png");

        // Выполнение
        Message result = chatService.sendMessageWithAttachment(msg, attach);

        // Проверка: оба объекта сохранены и связаны
        Assertions.assertNotNull(result.getId());
        Assertions.assertNotNull(attach.getId());
        Assertions.assertEquals(result.getId(), attach.getMessage().getId());
    }

    // === Вспомогательные методы ===

    /// Получить роль "USER" или создать её.
    private Role getOrCreateUserRole() {
        return roleRepository.findByTitle("USER")
                .orElseGet(() -> {
                    Role role = new Role();
                    role.setTitle("USER");
                    role.setDescription("Обычный пользователь");
                    role.setLevel(1);
                    return roleRepository.save(role);
                });
    }

    /// Создать тестового пользователя.
    private User createUser(String username, Role role) {
        User user = new User();
        user.setUsername(username + "_" + System.currentTimeMillis());
        user.setPassword("test_pass");
        user.setRole(role);
        return userRepository.save(user);
    }

    /// Создать тестовую комнату.
    private ChatRoom createChatRoom(String name) {
        ChatRoom room = new ChatRoom();
        room.setName(name + "_" + System.currentTimeMillis());
        room.setCreatedDate(LocalDateTime.now());
        room.setIsPrivate(false);
        return chatRoomRepository.save(room);
    }
}