package ru.matveyder.NauJava.runner;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import ru.matveyder.NauJava.entity.User;
import ru.matveyder.NauJava.service.UserService;

import java.util.List;
import java.util.Optional;

/**
 * Компонент для обработки консольных команд пользователя.
 * Парсит ввод, проверяет формат аргументов и вызывает методы бизнес-логики (UserService).
 */
@Component
public class CommandProcessor {

    private final UserService userService;

    /**
     * Конструктор с внедрением зависимости сервиса пользователей.
     * @param userService сервис для управления пользователями
     */
    @Autowired
    public CommandProcessor(UserService userService) {
        this.userService = userService;
    }

    /**
     * Основной метод обработки входной строки команды.
     * Разбивает строку на части, определяет тип команды и выполняет соответствующее действие.
     *
     * @param input строка ввода от пользователя
     */
    public void processCommand(String input) {
        String[] parts = input.trim().split(" ");
        if (parts.length == 0 || parts[0].isEmpty()) {
            return;
        }

        String command = parts[0].toLowerCase();

        try {
            switch (command) {
                case "create":
                    if (parts.length >= 4) {
                        String login = parts[1];
                        String password = parts[2];
                        String email = parts[3];
                        userService.registerUser(login, password, email);
                        System.out.println("Пользователь создан: " + login);
                    } else {
                        System.out.println("Ошибка: Неверный формат. Используйте: create <login> <password> <email>");
                    }
                    break;

                case "get":
                    if (parts.length >= 2) {
                        Long id = Long.parseLong(parts[1]);
                        Optional<User> userOpt = userService.getUserById(id);
                        if (userOpt.isPresent()) {
                            System.out.println("Пользователь найден: " + userOpt.get());
                        } else {
                            System.out.println("Пользователь с ID " + id + " не найден.");
                        }
                    } else {
                        System.out.println("Ошибка: Неверный формат. Используйте: get <id>");
                    }
                    break;

                case "list":
                    List<User> users = userService.getAllUsers();
                    if (users.isEmpty()) {
                        System.out.println("Список пользователей пуст.");
                    } else {
                        System.out.println("Список пользователей:");
                        for (User u : users) {
                            System.out.println(u);
                        }
                    }
                    break;

                case "delete":
                    if (parts.length >= 2) {
                        Long id = Long.parseLong(parts[1]);
                        userService.deleteUser(id);
                        System.out.println("Пользователь с ID " + id + " удален.");
                    } else {
                        System.out.println("Ошибка: Неверный формат. Используйте: delete <id>");
                    }
                    break;

                case "changepass":
                    if (parts.length >= 3) {
                        Long id = Long.parseLong(parts[1]);
                        String newPass = parts[2];
                        userService.changePassword(id, newPass);
                        System.out.println("Пароль изменен для пользователя с ID " + id);
                    } else {
                        System.out.println("Ошибка: Неверный формат. Используйте: changepass <id> <new_password>");
                    }
                    break;

                case "help":
                    printHelp();
                    break;

                case "exit":
                    System.out.println("Выход из приложения...");
                    System.exit(0);
                    break;

                default:
                    System.out.println("Неизвестная команда. Введите 'help' для списка команд.");
            }
        } catch (NumberFormatException e) {
            System.out.println("Ошибка: ID должен быть числом.");
        } catch (IllegalArgumentException e) {
            System.out.println("Ошибка: " + e.getMessage());
        } catch (Exception e) {
            System.out.println("Произошла ошибка: " + e.getMessage());
        }
    }

    /**
     * Выводит справку по доступным командам в консоль.
     */
    private void printHelp() {
        System.out.println("--- Доступные команды ---");
        System.out.println("create <login> <password> <email> - Создать нового пользователя");
        System.out.println("get <id>                     - Получить пользователя по ID");
        System.out.println("list                         - Показать всех пользователей");
        System.out.println("delete <id>                  - Удалить пользователя по ID");
        System.out.println("changepass <id> <pass>       - Изменить пароль пользователю");
        System.out.println("help                         - Показать эту справку");
        System.out.println("exit                         - Выйти из приложения");
        System.out.println("-----------------------------");
    }
}