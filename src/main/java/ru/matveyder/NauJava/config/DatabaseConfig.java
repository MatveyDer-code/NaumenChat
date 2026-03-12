package ru.matveyder.NauJava.config;

import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;
import ru.matveyder.NauJava.entity.User;

import java.util.ArrayList;
import java.util.List;

/**
 * Класс конфигурации для создания и настройки бинов, имитирующих слой доступа к данным.
 * Предоставляет единственный экземпляр (Singleton}) списка для хранения
 * объектов сущности {@link User}, выступая в роли оперативной памяти вместо реальной СУБД.
 */
@Configuration
public class DatabaseConfig {

    @Bean
    @Scope(value = BeanDefinition.SCOPE_SINGLETON)
    public List<User> userDatabase() {
        return new ArrayList<>();
    }
}