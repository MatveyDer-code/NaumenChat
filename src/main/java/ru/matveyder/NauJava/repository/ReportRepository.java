package ru.matveyder.NauJava.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import ru.matveyder.NauJava.entity.Report;

/**
 * Репозиторий для работы с сущностью Report.
 */
@RepositoryRestResource(path = "reports")
public interface ReportRepository extends JpaRepository<Report, Long> {
}