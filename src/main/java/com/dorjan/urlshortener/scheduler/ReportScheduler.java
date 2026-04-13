package com.dorjan.urlshortener.scheduler;

import com.dorjan.urlshortener.config.UrlShortenerProperties;
import com.dorjan.urlshortener.model.Url;
import com.dorjan.urlshortener.service.StorageService;
import com.dorjan.urlshortener.service.UrlService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class ReportScheduler {

    private final UrlService urlService;
    private final StorageService storageService;
    private final UrlShortenerProperties properties;

    @Scheduled(cron = "${report.scheduler.cron}")
    public void generateDailyReport() {
        log.info("Generating daily URL report...");

        List<Url> urls = urlService.getAllUrls();
        String fileName = "url-report-" + LocalDate.now().format(DateTimeFormatter.ISO_LOCAL_DATE) + ".csv";
        Path tempFile = null;

        try {
            tempFile = Files.createTempFile("url-report-", ".csv");

            try (CSVPrinter csvPrinter = new CSVPrinter(Files.newBufferedWriter(tempFile),
                    CSVFormat.DEFAULT.builder()
                            .setHeader("urlId", "shortUrl", "longUrl", "totalClicks")
                            .build())) {

                for (Url url : urls) {
                    csvPrinter.printRecord(
                            url.getId(),
                            properties.baseUrl() + "/" + url.getShortUrl(),
                            url.getLongUrl(),
                            url.getClickCount()
                    );
                }
            }

            storageService.uploadFile(fileName, tempFile.toFile());
            log.info("Daily URL report uploaded: {}", fileName);

        } catch (Exception e) {
            log.error("Error generating CSV report", e);
        } finally {
            try {
                if (tempFile != null) {
                    Files.deleteIfExists(tempFile);
                }
            } catch (Exception e) {
                log.warn("Failed to delete temp file", e);
            }
        }
    }
}
