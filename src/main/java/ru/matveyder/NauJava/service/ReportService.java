package ru.matveyder.NauJava.service;

import org.springframework.stereotype.Service;
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

    public ReportService(ReportRepository reportRepository,
                         UserRepository userRepository,
                         MessageRepository messageRepository) {
        this.reportRepository = reportRepository;
        this.userRepository = userRepository;
        this.messageRepository = messageRepository;
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
            long totalStart = System.currentTimeMillis();

            try {
                /// Поток для количества пользователей
                CompletableFuture<Long> userCountFuture = CompletableFuture.supplyAsync(() -> {
                    long start = System.currentTimeMillis();
                    long count = userRepository.count();
                    long elapsed = System.currentTimeMillis() - start;
                    System.out.println("Время подсчета пользователей: " + elapsed + " мс");
                    return count;
                });

                /// Поток для списка сообщений
                CompletableFuture<List<Message>> messagesFuture = CompletableFuture.supplyAsync(() -> {
                    long start = System.currentTimeMillis();
                    List<Message> messages = messageRepository.findAll();
                    long elapsed = System.currentTimeMillis() - start;
                    System.out.println("Время получения сообщений: " + elapsed + " мс");
                    return messages;
                });

                /// Ждем завершения обоих потоков
                Long userCount = userCountFuture.join();
                List<Message> messages = messagesFuture.join();

                /// Формируем HTML-отчет
                StringBuilder html = new StringBuilder();
                html.append("<html><head><title>Отчет системы</title></head><body>");
                html.append("<h2>Статистика пользователей</h2>");
                html.append("<p>Количество зарегистрированных пользователей: ").append(userCount).append("</p>");

                html.append("<h2>Сообщения</h2>");
                html.append("<table border='1'><tr><th>Автор</th><th>Сообщение</th><th>Дата отправки</th></tr>");
                for (Message m : messages) {
                    html.append("<tr>")
                            .append("<td>").append(m.getAuthor().getUsername()).append("</td>")
                            .append("<td>").append(m.getContent()).append("</td>")
                            .append("<td>").append(m.getSendDate()).append("</td>")
                            .append("</tr>");
                }
                html.append("</table>");

                long totalElapsed = System.currentTimeMillis() - totalStart;
                html.append("<p>Общее время формирования отчета: ").append(totalElapsed).append(" мс</p>");
                html.append("</body></html>");

                /// Сохраняем результат в БД
                reportRepository.findById(reportId).ifPresent(report -> {
                    report.setContent(html.toString());
                    report.setStatus(ReportStatus.COMPLETED);
                    reportRepository.save(report);
                });

            } catch (Exception e) {
                /// В случае ошибки меняем статус отчета
                reportRepository.findById(reportId).ifPresent(report -> {
                    report.setStatus(ReportStatus.ERROR);
                    reportRepository.save(report);
                });
                e.printStackTrace();
            }
        });
    }
}