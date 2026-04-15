package com.dorjan.urlshortener.scheduler;

import com.dorjan.urlshortener.config.UrlShortenerProperties;
import com.dorjan.urlshortener.model.Url;
import com.dorjan.urlshortener.service.StorageService;
import com.dorjan.urlshortener.service.UrlService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.File;
import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ReportSchedulerTest {

    @Mock
    private UrlService urlService;

    @Mock
    private StorageService storageService;

    @Mock
    private UrlShortenerProperties properties;

    @InjectMocks
    private ReportScheduler reportScheduler;

    @Test
    void generateDailyReport_shouldGenerateAndUpload() {
        Url url = new Url();
        url.setId(1L);
        url.setShortUrl("abc123");
        url.setLongUrl("https://www.google.com");
        url.setCreatedAt(LocalDateTime.now());
        url.setExpiresAt(LocalDateTime.now().plusMinutes(5));
        url.setClickCount(3);

        when(urlService.getAllUrls()).thenReturn(List.of(url));
        when(properties.baseUrl()).thenReturn("https://localhost:8080");

        reportScheduler.generateDailyReport();

        verify(storageService).uploadFile(anyString(), any(File.class));
    }

    @Test
    void generateDailyReport_shouldHandleEmptyList() {
        when(urlService.getAllUrls()).thenReturn(List.of());

        reportScheduler.generateDailyReport();

        verify(storageService).uploadFile(anyString(), any(File.class));
    }
}
