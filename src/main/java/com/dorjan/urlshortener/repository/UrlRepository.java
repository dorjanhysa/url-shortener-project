package com.dorjan.urlshortener.repository;

import com.dorjan.urlshortener.model.Url;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.Optional;

@Repository
public interface UrlRepository extends JpaRepository<Url, Long> {

    Optional<Url> findByShortUrl(String shortUrl);

    Optional<Url> findByLongUrlAndExpiresAtAfter(String longUrl, LocalDateTime now);

    void deleteByExpiresAtBefore(LocalDateTime now);

    @Modifying
    @Transactional
    @Query("UPDATE Url u SET u.clickCount = u.clickCount + 1 WHERE u.shortUrl = :shortUrl")
    void incrementClickCount(@Param("shortUrl") String shortUrl);
}
