package ru.matveyder.NauJava.service;

import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;
import ru.matveyder.NauJava.entity.Report;
import ru.matveyder.NauJava.entity.utility.ReportStatus;
import ru.matveyder.NauJava.entity.Message;
import ru.matveyder.NauJava.repository.ReportRepository;
import ru.matveyder.NauJava.repository.UserRepository;
import ru.matveyder.NauJava.repository.MessageRepository;

import java.util.List;
import java.util.concurrent.CompletableFuture;

/**
 * Сервис для работы с отчетами.
 * Реализует асинхронное формирование HTML-отчета с использованием многопоточности.
 */
@Service
public class ReportService {

    private final ReportRepository reportRepository;
    private final UserRepository userRepository;
    private final MessageRepository messageRepository;
    private final TemplateEngine templateEngine;

    public ReportService(ReportRepository reportRepository,
                         UserRepository userRepository,
                         MessageRepository messageRepository,
                         TemplateEngine templateEngine) {
        this.reportRepository = reportRepository;
        this.userRepository = userRepository;
        this.messageRepository = messageRepository;
        this.templateEngine = templateEngine;
    }

    /**
     * Создает новый отчет в БД со статусом "создан".
     * @return идентификатор созданного отчета
     */
    public Long createReport() {
        Report report = new Report();
        report.setStatus(ReportStatus.CREATED);
        report.setContent("");
        reportRepository.save(report);
        return report.getId();
    }

    /**
     * Получение отчета по его ID.
     * @param reportId ID отчета
     * @return отчет
     */
    public Report getReportContent(Long reportId) {
        return reportRepository.findById(reportId).orElse(null);
    }

    /**
     * Асинхронное формирование отчета с подсчетом времени.
     * Количество пользователей и список сообщений вычисляются в отдельных потоках.
     * @param reportId ID отчета
     */
    public CompletableFuture<Void> generateReportAsync(Long reportId) {
        return CompletableFuture.runAsync(() -> {
            try {
                // Переменные для хранения времени выполнения
                final long[] userCountDuration = {0};
                final long[] messagesDuration = {0};

                // Поток для подсчета пользователей
                CompletableFuture<Long> userCountFuture = CompletableFuture.supplyAsync(() -> {
                    long start = System.currentTimeMillis();
                    long count = userRepository.count();
                    userCountDuration[0] = System.currentTimeMillis() - start;
                    return count;
                });

                // Поток для получения сообщений
                CompletableFuture<List<Message>> messagesFuture = CompletableFuture.supplyAsync(() -> {
                    long start = System.currentTimeMillis();
                    List<Message> messages = messageRepository.findAll();
                    messagesDuration[0] = System.currentTimeMillis() - start;
                    return messages;
                });

                // Ждем завершения обоих потоков
                Long userCount = userCountFuture.join();
                List<Message> messages = messagesFuture.join();

                // Общее время = максимум из двух (т.к. они выполнялись параллельно)
                long totalElapsed = Math.max(userCountDuration[0], messagesDuration[0]);

                // Формируем HTML-отчет через Thymeleaf
                Context context = new Context();
                context.setVariable("userCount", userCount);
                context.setVariable("userCountDuration", userCountDuration[0]);
                context.setVariable("messages", messages);
                context.setVariable("messagesDuration", messagesDuration[0]);
                context.setVariable("totalElapsed", totalElapsed);

                String htmlContent = templateEngine.process("report", context);

                // Сохраняем результат в БД
                reportRepository.findById(reportId).ifPresent(report -> {
                    report.setContent(htmlContent);
                    report.setStatus(ReportStatus.COMPLETED);
                    reportRepository.save(report);
                });

            } catch (Exception e) {
                // В случае ошибки меняем статус отчета
                reportRepository.findById(reportId).ifPresent(report -> {
                    report.setStatus(ReportStatus.ERROR);
                    reportRepository.save(report);
                });
                e.printStackTrace();
            }
        });
    }
}