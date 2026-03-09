package ru.matveyder.NauJava.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import jakarta.annotation.PostConstruct;

/**
 * Класс конфигурации для управления свойствами приложения.
 * Отвечает за внедрение значений имени и версии приложения из файла
 * {@code application.properties} и их вывод в консоль после инициализации контекста Spring.
 */
@Configuration
public class AppPropertiesConfig {

    /**
     * Имя приложения, внедряемое из свойства {@code app.name}.
     */
    @Value("${app.name}")
    private String appName;

    /**
     * Версия приложения, внедряемая из свойства {@code app.version}.
     */
    @Value("${app.version}")
    private String appVersion;

    /**
     * Метод инициализации, вызываемый автоматически после создания бина и внедрения всех зависимостей.
     * Выводит в стандартный поток вывода (консоль) имя и версию запущенного приложения
     * в форматированном виде.
     */
    @PostConstruct
    public void init() {
        System.out.println("=================================");
        System.out.println("Запуск приложения: " + appName);
        System.out.println("Версия: " + appVersion);
        System.out.println("=================================");
    }
}