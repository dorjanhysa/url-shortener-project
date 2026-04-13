package com.dorjan.urlshortener.service;

import com.dorjan.urlshortener.config.MinioProperties;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.CreateBucketRequest;
import software.amazon.awssdk.services.s3.model.HeadBucketRequest;
import software.amazon.awssdk.services.s3.model.NoSuchBucketException;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

import java.io.File;

@Service
@RequiredArgsConstructor
@Slf4j
public class StorageService {

    private final S3Client s3Client;
    private final MinioProperties minioProperties;

    @PostConstruct
    public void init() {
        createBucketIfNotExists();
    }

    public void uploadFile(String key, File file) {
        log.info("Uploading file: {} to bucket: {}", key, minioProperties.bucketName());
        PutObjectRequest request = PutObjectRequest.builder()
                .bucket(minioProperties.bucketName())
                .key(key)
                .contentType("text/csv")
                .build();

        s3Client.putObject(request, RequestBody.fromFile(file));
        log.info("Uploaded file: {} to bucket: {}", key, minioProperties.bucketName());
    }

    private void createBucketIfNotExists() {
        try {
            s3Client.headBucket(HeadBucketRequest.builder()
                    .bucket(minioProperties.bucketName())
                    .build());
            log.info("Bucket already exists: {}", minioProperties.bucketName());
        } catch (NoSuchBucketException e) {
            s3Client.createBucket(CreateBucketRequest.builder()
                    .bucket(minioProperties.bucketName())
                    .build());
            log.info("Created bucket: {}", minioProperties.bucketName());
        }
    }
}
