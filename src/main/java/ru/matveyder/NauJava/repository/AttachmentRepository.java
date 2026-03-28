package ru.matveyder.NauJava.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import ru.matveyder.NauJava.entity.Attachment;

/**
 * Репозиторий для работы с сущностью Attachment.
 */

@RepositoryRestResource(path = "attachments")
public interface AttachmentRepository extends JpaRepository<Attachment, Long> {
}