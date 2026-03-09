package ru.matveyder.NauJava.runner;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.boot.CommandLineRunner;

import java.util.Scanner;

/**
 * Класс конфигурации для запуска консольного интерфейса.
 * Создает бин CommandLineRunner, который открывает поток ввода Scanner
 * и передает команды процессору обработки.
 */
@Configuration
public class ConsoleRunner {

    @Autowired
    private CommandProcessor commandProcessor;

    /**
     * Создает и возвращает реализацию CommandLineRunner.
     * Запускает бесконечный цикл чтения команд из консоли до ввода команды 'exit'.
     *
     * @return лямбда-выражение, реализующее интерфейс CommandLineRunner
     */
    @Bean
    public CommandLineRunner commandLineRunner() {
        return args -> {
            Scanner scanner = new Scanner(System.in);
            System.out.println("Приложение запущено. Введите 'help' для списка команд.");

            while (true) {
                System.out.print("> ");
                if (scanner.hasNextLine()) {
                    String input = scanner.nextLine();
                    if ("exit".equalsIgnoreCase(input.trim())) {
                        commandProcessor.processCommand(input);
                        break;
                    }
                    commandProcessor.processCommand(input);
                }
            }
            scanner.close();
        };
    }
}