package com.dorjan.urlshortener.controller;

import com.dorjan.urlshortener.BaseIntegrationTest;
import com.dorjan.urlshortener.dto.RegisterRequest;
import com.dorjan.urlshortener.dto.ShortenUrlRequest;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@AutoConfigureMockMvc
class UrlControllerIntegrationTest extends BaseIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    private String token;

    @BeforeEach
    void setUp() throws Exception {
        String username = "urluser" + System.currentTimeMillis();
        RegisterRequest register = new RegisterRequest();
        register.setUsername(username);
        register.setPassword("test123");

        MvcResult result = mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(register)))
                .andReturn();

        String response = result.getResponse().getContentAsString();
        token = objectMapper.readTree(response).get("token").asText();
    }

    @Test
    void shortenUrl_shouldReturn201WithToken() throws Exception {
        ShortenUrlRequest request = new ShortenUrlRequest();
        request.setLongUrl("https://www.google.com");

        mockMvc.perform(post("/api/urls/shorten")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.shortUrl").isNotEmpty())
                .andExpect(jsonPath("$.longUrl").value("https://www.google.com"));
    }

    @Test
    void shortenUrl_shouldReturn403WithoutToken() throws Exception {
        ShortenUrlRequest request = new ShortenUrlRequest();
        request.setLongUrl("https://www.google.com");

        mockMvc.perform(post("/api/urls/shorten")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isForbidden());
    }

    @Test
    void getStats_shouldReturnClickCount() throws Exception {
        ShortenUrlRequest request = new ShortenUrlRequest();
        request.setLongUrl("https://www.example.com");

        MvcResult result = mockMvc.perform(post("/api/urls/shorten")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andReturn();

        String response = result.getResponse().getContentAsString();
        String shortUrl = objectMapper.readTree(response).get("shortUrl").asText();
        String shortCode = shortUrl.substring(shortUrl.lastIndexOf("/") + 1);

        mockMvc.perform(get("/api/urls/" + shortCode + "/stats")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.clickCount").value(0));
    }

    @Test
    void redirect_shouldReturn302() throws Exception {
        ShortenUrlRequest request = new ShortenUrlRequest();
        request.setLongUrl("https://www.github.com");

        MvcResult result = mockMvc.perform(post("/api/urls/shorten")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andReturn();

        String response = result.getResponse().getContentAsString();
        String shortUrl = objectMapper.readTree(response).get("shortUrl").asText();
        String shortCode = shortUrl.substring(shortUrl.lastIndexOf("/") + 1);

        mockMvc.perform(get("/" + shortCode))
                .andExpect(status().isFound())
                .andExpect(header().string("Location", "https://www.github.com"));
    }
}
