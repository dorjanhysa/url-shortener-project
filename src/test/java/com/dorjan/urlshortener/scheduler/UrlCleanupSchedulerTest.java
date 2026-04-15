package com.dorjan.urlshortener.scheduler;

import com.dorjan.urlshortener.service.UrlService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class UrlCleanupSchedulerTest {

    @Mock
    private UrlService urlService;

    @InjectMocks
    private UrlCleanupScheduler urlCleanupScheduler;

    @Test
    void cleanupExpiredUrls_shouldCallService() {
        urlCleanupScheduler.cleanupExpiredUrls();

        verify(urlService).deleteExpiredUrls();
    }
}
