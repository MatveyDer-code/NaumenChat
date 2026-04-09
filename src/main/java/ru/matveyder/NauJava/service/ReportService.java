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
                /// Переменные для хранения результатов и времени выполнения
                final Long[] userCount = {0L};
                final long[] userCountDuration = {0};

                final List<Message>[] messagesArray = new List[]{null};
                final long[] messagesDuration = {0};

                /// Поток для подсчета пользователей (явное создание Thread)
                Thread userCountThread = new Thread(() -> {
                    long startTime = System.currentTimeMillis();
                    userCount[0] = userRepository.count();
                    long elapsed = System.currentTimeMillis() - startTime;
                    userCountDuration[0] = elapsed;
                    System.out.println("Время подсчета пользователей: " + elapsed + " мс");
                });

                /// Поток для получения сообщений (явное создание Thread)
                Thread messagesThread = new Thread(() -> {
                    long startTime = System.currentTimeMillis();
                    messagesArray[0] = messageRepository.findAll();
                    long elapsed = System.currentTimeMillis() - startTime;
                    messagesDuration[0] = elapsed;
                    System.out.println("Время получения сообщений: " + elapsed + " мс");
                });

                /// Запускаем оба потока
                userCountThread.start();
                messagesThread.start();

                /// Ждем завершения обоих потоков (join)
                userCountThread.join();
                messagesThread.join();

                List<Message> messages = messagesArray[0];

                /// Формируем HTML-отчет
                StringBuilder html = new StringBuilder();
                html.append("<html><head><title>Отчет системы</title></head><body>");

                html.append("<h2>Статистика пользователей</h2>");
                html.append("<p>Количество зарегистрированных пользователей: ").append(userCount[0]).append("</p>");
                html.append("<p>Время вычисления пункта 2: ").append(userCountDuration[0]).append(" мс</p>");

                html.append("<h2>Сообщения</h2>");
                html.append("<table border='1'><tr><th>Автор</th><th>Сообщение</th><th>Дата отправки</th></tr>");
                for (Message m : messages) {
                    String author = (m.getAuthor() != null) ? m.getAuthor().getUsername() : "Неизвестно";
                    html.append("<tr>")
                            .append("<td>").append(author).append("</td>")
                            .append("<td>").append(m.getContent() != null ? m.getContent() : "").append("</td>")
                            .append("<td>").append(m.getSendDate()).append("</td>")
                            .append("</tr>");
                }
                html.append("</table>");
                html.append("<p>Время вычисления пункта 3: ").append(messagesDuration[0]).append(" мс</p>");

                /// Общее время = максимум из двух (т.к. они выполнялись параллельно)
                long totalElapsed = Math.max(userCountDuration[0], messagesDuration[0]);
                html.append("<p>Общее время формирования отчета: ").append(totalElapsed).append(" мс</p>");
                html.append("</body></html>");

                /// Сохраняем результат в БД со статусом COMPLETED
                reportRepository.findById(reportId).ifPresent(report -> {
                    report.setContent(html.toString());
                    report.setStatus(ReportStatus.COMPLETED);
                    reportRepository.save(report);
                });

            } catch (Exception e) {
                /// В случае ошибки меняем статус отчета на ERROR
                reportRepository.findById(reportId).ifPresent(report -> {
                    report.setStatus(ReportStatus.ERROR);
                    reportRepository.save(report);
                });
                e.printStackTrace();
            }
        });
    }
}