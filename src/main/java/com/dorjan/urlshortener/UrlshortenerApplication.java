package com.dorjan.urlshortener;

import com.dorjan.urlshortener.config.JwtProperties;
import com.dorjan.urlshortener.config.MinioProperties;
import com.dorjan.urlshortener.config.UrlShortenerProperties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
@EnableConfigurationProperties({UrlShortenerProperties.class, JwtProperties.class, MinioProperties.class})
public class UrlshortenerApplication {

	public static void main(String[] args) {
		SpringApplication.run(UrlshortenerApplication.class, args);
	}

}
