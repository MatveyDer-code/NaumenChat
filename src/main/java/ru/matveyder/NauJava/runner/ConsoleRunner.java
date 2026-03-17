package ru.matveyder.NauJava.runner;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.boot.CommandLineRunner;

import java.util.Scanner;

/**
 * Класс конфигурации для запуска консольного интерфейса.
 */
@Configuration
public class ConsoleRunner {

    @Autowired(required = false)
    private CommandProcessor commandProcessor;

    @Bean
    public CommandLineRunner commandLineRunner() {
        return args -> {
            if (commandProcessor == null) {
                return;
            }

            Scanner scanner = new Scanner(System.in);
            System.out.println("Приложение запущено. Введите 'help' для списка команд.");

            while (scanner.hasNextLine()) {
                System.out.print("> ");
                String input = scanner.nextLine();

                if ("exit".equalsIgnoreCase(input.trim())) {
                    break;
                }

                if (commandProcessor != null) {
                    commandProcessor.processCommand(input);
                }
            }
            scanner.close();
        };
    }
}