package ru.matveyder.NauJava.controller;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Глобальный обработчик ошибок для REST контроллеров.
 * Унифицирует ответы при ошибках (пункт 5 задания).
 */
@RestControllerAdvice
public class ErrorControllerAdvice {

    private static final Logger log = LoggerFactory.getLogger(ErrorControllerAdvice.class);

    /// Обработка ошибок валидации (неверные данные от клиента) -> 400
    @ExceptionHandler(IllegalArgumentException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleIllegalArgument(IllegalArgumentException e) {
        log.warn("Ошибка валидации данных: {}", e.getMessage());
        return new ErrorResponse(e.getMessage());
    }

    /// Обработка ситуации, когда ресурс не найден -> 404
    @ExceptionHandler({java.util.NoSuchElementException.class, org.springframework.dao.EmptyResultDataAccessException.class})
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorResponse handleNotFound(Exception e) {
        log.warn("Ресурс не найден: {}", e.getMessage());
        return new ErrorResponse("Ресурс не найден");
    }

    /// Обработка всех остальных критических ошибок -> 500
    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ErrorResponse handleException(Exception e) {
        log.error("Внутренняя ошибка сервера", e);
        return new ErrorResponse("Произошла внутренняя ошибка сервера");
    }

    /// Простая запись для ответа
    public record ErrorResponse(String message) {}
}