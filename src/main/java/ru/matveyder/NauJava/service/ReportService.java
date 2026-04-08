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
            try {
                /// Поток для подсчета пользователей
                CompletableFuture<Long> userCountFuture = CompletableFuture.supplyAsync(() -> {
                    long start = System.currentTimeMillis();
                    long count = userRepository.count();
                    long duration = System.currentTimeMillis() - start;
                    System.out.println("Время подсчета пользователей: " + duration + " мс");
                    return count;
                });

                /// Поток для получения сообщений
                CompletableFuture<List<Message>> messagesFuture = CompletableFuture.supplyAsync(() -> {
                    long start = System.currentTimeMillis();
                    List<Message> messages = messageRepository.findAll();
                    long duration = System.currentTimeMillis() - start;
                    System.out.println("Время получения сообщений: " + duration + " мс");
                    return messages;
                });

                /// Ждем завершения потоков и измеряем время
                long startPoint2 = System.currentTimeMillis();
                Long userCount = userCountFuture.join();
                long durationPoint2 = System.currentTimeMillis() - startPoint2;

                long startPoint3 = System.currentTimeMillis();
                List<Message> messages = messagesFuture.join();
                long durationPoint3 = System.currentTimeMillis() - startPoint3;

                /// Формируем HTML-отчет
                StringBuilder html = new StringBuilder();
                html.append("<html><head><title>Отчет системы</title></head><body>");

                html.append("<h2>Статистика пользователей</h2>");
                html.append("<p>Количество зарегистрированных пользователей: ").append(userCount).append("</p>");
                html.append("<p>Время вычисления пункта 2: ").append(durationPoint2).append(" мс</p>");

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
                html.append("<p>Время вычисления пункта 3: ").append(durationPoint3).append(" мс</p>");

                long totalElapsed = durationPoint2 + durationPoint3;
                html.append("<p>Общее время формирования отчета: ").append(totalElapsed).append(" мс</p>");
                html.append("</body></html>");

                // Сохраняем результат в БД
                reportRepository.findById(reportId).ifPresent(report -> {
                    report.setContent(html.toString());
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