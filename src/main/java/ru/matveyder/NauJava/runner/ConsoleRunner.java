package ru.matveyder.NauJava.runner;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.boot.CommandLineRunner;

import java.util.Scanner;

@Configuration
public class ConsoleRunner {

    @Autowired
    private CommandProcessor commandProcessor;

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