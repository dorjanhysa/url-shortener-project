package com.dorjan.urlshortener.repository;

import com.dorjan.urlshortener.model.Url;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.Optional;

@Repository
public interface UrlRepository extends JpaRepository<Url, Long> {

    Optional<Url> findByShortUrl(String shortUrl);

    Optional<Url> findByLongUrlAndExpiresAtAfter(String longUrl, LocalDateTime now);

    void deleteByExpiresAtBefore(LocalDateTime now);
}
