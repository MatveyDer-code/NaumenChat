package ru.matveyder.NauJava.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import ru.matveyder.NauJava.service.ReportService;

/**
 * REST-контроллер для работы с отчетами.
 * Формирование и получение отчетов доступно только пользователям с ролью ADMIN.
 */
@RestController
@RequestMapping("/reports")
public class ReportController {

    private final ReportService reportService;

    public ReportController(ReportService reportService) {
        this.reportService = reportService;
    }

    /**
     * Создает отчет и запускает его асинхронное формирование.
     * @return ID созданного отчета
     */
    @PostMapping("/create")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Long> createReport() {
        Long reportId = reportService.createReport();
        reportService.generateReportAsync(reportId);
        return ResponseEntity.ok(reportId);
    }

    /**
     * Получение содержимого отчета по ID.
     * @param reportId ID отчета
     * @return HTML содержимое отчета или сообщение о статусе
     */
    @GetMapping("/{reportId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<String> getReport(@PathVariable Long reportId) {
        String content = reportService.getReportContent(reportId);
        return ResponseEntity.ok(content);
    }
}