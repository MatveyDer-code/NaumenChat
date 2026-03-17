package ru.matveyder.NauJava.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.matveyder.NauJava.entity.Attachment;

/**
 * Репозиторий для работы с сущностью Attachment.
 */
public interface AttachmentRepository extends JpaRepository<Attachment, Long> {
}