package ru.matveyder.NauJava.repository;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase;
import ru.matveyder.NauJava.entity.*;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Автотесты для MessageRepository.
 * Проверяет Query Methods, @Query и Criteria API методы.
 */
@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class MessageRepositoryTest {

    /// Репозиторий для работы с сообщениями.
    private final MessageRepository messageRepository;
    /// Репозиторий для работы с пользователями.
    private final UserRepository userRepository;
    /// Репозиторий для работы с комнатами чата.
    private final ChatRoomRepository chatRoomRepository;
    /// Репозиторий для работы с ролями.
    private final RoleRepository roleRepository;

    /// Конструктор с внедрением зависимостей через Spring.
    @Autowired
    MessageRepositoryTest(MessageRepository messageRepository,
                          UserRepository userRepository,
                          ChatRoomRepository chatRoomRepository,
                          RoleRepository roleRepository) {
        this.messageRepository = messageRepository;
        this.userRepository = userRepository;
        this.chatRoomRepository = chatRoomRepository;
        this.roleRepository = roleRepository;
    }

    /// Тест Query Method: поиск сообщений по автору и статусу прочтения.
    @Test
    void testFindByAuthorIdAndIsRead_QueryMethod() {
        Role role = getOrCreateUserRole();
        User author = createUser("qm_author", role);
        ChatRoom room = createChatRoom("QM_TestRoom");

        Message msg = new Message();
        msg.setContent("QM Test Message");
        msg.setSendDate(LocalDateTime.now());
        msg.setIsRead(false);
        msg.setAuthor(author);
        msg.setChatRoom(room);
        messageRepository.save(msg);

        List<Message> found = messageRepository.findByAuthorIdAndIsRead(author.getId(), false);

        Assertions.assertFalse(found.isEmpty());
        Assertions.assertEquals("QM Test Message", found.getFirst().getContent());
    }

    /// Тест @Query + JPQL: поиск сообщений по названию комнаты (через связанную сущность).
    @Test
    void testFindByChatRoomName_JPQL() {
        ChatRoom room = createChatRoom("JPQL_TestRoom");
        Role role = getOrCreateUserRole();
        User author = createUser("jpql_author", role);

        Message msg = new Message();
        msg.setContent("JPQL Test Content");
        msg.setSendDate(LocalDateTime.now());
        msg.setChatRoom(room);
        msg.setAuthor(author);
        messageRepository.save(msg);

        List<Message> found = messageRepository.findByChatRoomName(room.getName());

        Assertions.assertFalse(found.isEmpty());
        Assertions.assertEquals("JPQL Test Content", found.getFirst().getContent());
    }

    /// Тест Criteria API: поиск сообщений по автору и статусу (аналог Query Method).
    @Test
    void testFindByAuthorIdAndIsRead_CriteriaAPI() {
        Role role = getOrCreateUserRole();
        User author = createUser("criteria_author", role);
        ChatRoom room = createChatRoom("Criteria_TestRoom");

        Message msg = new Message();
        msg.setContent("Criteria API Test");
        msg.setSendDate(LocalDateTime.now());
        msg.setIsRead(true);
        msg.setAuthor(author);
        msg.setChatRoom(room);
        messageRepository.save(msg);

        List<Message> found = messageRepository.findByAuthorIdAndIsRead(author.getId(), true);

        Assertions.assertFalse(found.isEmpty());
        Assertions.assertEquals("Criteria API Test", found.getFirst().getContent());
    }

    /// Тест Criteria API: поиск сообщений по названию комнаты с JOIN.
    @Test
    void testFindByChatRoomName_CriteriaAPI() {
        ChatRoom room = createChatRoom("Criteria_JoinRoom");
        Role role = getOrCreateUserRole();
        User author = createUser("criteria_join_author", role);

        Message msg = new Message();
        msg.setContent("Criteria JOIN Test");
        msg.setSendDate(LocalDateTime.now());
        msg.setChatRoom(room);
        msg.setAuthor(author);
        messageRepository.save(msg);

        List<Message> found = messageRepository.findByChatRoomName(room.getName());

        Assertions.assertFalse(found.isEmpty());
        Assertions.assertEquals("Criteria JOIN Test", found.getFirst().getContent());
    }

    // === Вспомогательные методы ===

    /// Получить роль "USER" или создать её, если не существует.
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

    /// Создать тестового пользователя с заданным логином и ролью.
    private User createUser(String username, Role role) {
        User user = new User();
        user.setUsername(username + "_" + System.currentTimeMillis());
        user.setPassword("test_pass");
        user.setRole(role);
        return userRepository.save(user);
    }

    /// Создать тестовую комнату чата с заданным названием.
    private ChatRoom createChatRoom(String name) {
        ChatRoom room = new ChatRoom();
        room.setName(name + "_" + System.currentTimeMillis());
        room.setCreatedDate(LocalDateTime.now());
        room.setIsPrivate(false);
        return chatRoomRepository.save(room);
    }

    /// Тест метода existsByContentPrefix: проверка существования сообщения по префиксу.
    @Test
    void testExistsByContentPrefix() {
        Role role = getOrCreateUserRole();
        User author = createUser("exists_author", role);
        ChatRoom room = createChatRoom("ExistsRoom");

        /// Создаем сообщение с уникальным контентом
        String uniqueContent = "PREFIX_TEST_" + System.currentTimeMillis();
        Message msg = new Message();
        msg.setContent(uniqueContent);
        msg.setSendDate(LocalDateTime.now());
        msg.setAuthor(author);
        msg.setChatRoom(room);
        messageRepository.save(msg);

        /// Точное совпадение
        boolean foundExact = messageRepository.existsByContentPrefix(uniqueContent);
        Assertions.assertTrue(foundExact, "Сообщение с точным контентом не найдено");

        /// Проверка, что НЕ существует
        boolean foundFake = messageRepository.existsByContentPrefix("FAKE_PREFIX_12345");
        Assertions.assertFalse(foundFake, "Найдено несуществующее сообщение");
    }
}