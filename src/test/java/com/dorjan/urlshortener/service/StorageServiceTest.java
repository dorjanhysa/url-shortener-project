package com.dorjan.urlshortener.service;

import com.dorjan.urlshortener.config.MinioProperties;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.*;

import java.io.File;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class StorageServiceTest {

    @Mock
    private S3Client s3Client;

    @Mock
    private MinioProperties minioProperties;

    @InjectMocks
    private StorageService storageService;

    @Test
    void uploadFile_shouldUploadToS3() throws Exception {
        when(minioProperties.bucketName()).thenReturn("url-reports");
        when(s3Client.putObject(any(PutObjectRequest.class), any(RequestBody.class)))
                .thenReturn(PutObjectResponse.builder().build());

        File tempFile = File.createTempFile("test-report", "csv");
        tempFile.deleteOnExit();

        storageService.uploadFile("test-report.csv", tempFile);

        verify(s3Client).putObject(any(PutObjectRequest.class), any(RequestBody.class));
    }

    @Test
    void init_shouldCreateBucketWhenNotExists() {
        when(minioProperties.bucketName()).thenReturn("url-reports");
        when(s3Client.headBucket(any(HeadBucketRequest.class)))
                .thenThrow(NoSuchBucketException.builder().build());
        when(s3Client.createBucket(any(CreateBucketRequest.class)))
                .thenReturn(CreateBucketResponse.builder().build());

        storageService.init();

        verify(s3Client).createBucket(any(CreateBucketRequest.class));
    }

    @Test
    void init_shouldSkipCreationWhenBucketExists() {
        when(minioProperties.bucketName()).thenReturn("url-reports");
        when(s3Client.headBucket(any(HeadBucketRequest.class)))
                .thenReturn(HeadBucketResponse.builder().build());

        storageService.init();

        verify(s3Client, never()).createBucket(any(CreateBucketRequest.class));
    }
}
