package ru.matveyder.NauJava.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import jakarta.annotation.PostConstruct;

@Configuration
public class AppPropertiesConfig {

    @Value("${app.name}")
    private String appName;

    @Value("${app.version}")
    private String appVersion;

    @PostConstruct
    public void init() {
        System.out.println("=================================");
        System.out.println("Запуск приложения: " + appName);
        System.out.println("Версия: " + appVersion);
        System.out.println("=================================");
    }
}