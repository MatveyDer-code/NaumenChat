package ru.matveyder.NauJava.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import ru.matveyder.NauJava.entity.Report;
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
    @GetMapping("/{reportId:[0-9]+}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<String> getReport(@PathVariable Long reportId) {
        Report report = reportService.getReportContent(reportId);

        if (report == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Отчет не найден");
        }

        switch (report.getStatus()) {
            case CREATED:
                return ResponseEntity.status(HttpStatus.ACCEPTED).body("Отчет еще формируется...");
            case ERROR:
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Ошибка при формировании отчета");
            case COMPLETED:
                return ResponseEntity.ok(report.getContent());
            default:
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Неизвестный статус отчета");
        }
    }
}