package ru.matveyder.NauJava.entity.DTO;

public record MessageDto(
        Long id,
        String content,
        Long authorId,
        Long chatRoomId,
        Boolean isRead
) {}