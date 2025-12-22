package com.Microservice.AiServices.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.Map;

@Service
public class GeminiService {

    private final WebClient webClient;
    private final String geminiApiKey;

    /**
     * Constructor-based injection (BEST PRACTICE)
     */
    public GeminiService(
            WebClient.Builder webClientBuilder,
            @Value("${GEMINI_API_URL}") String geminiApiUrl,
            @Value("${GEMINI_API_KEY}") String geminiApiKey) {

        this.geminiApiKey = geminiApiKey;

        this.webClient = webClientBuilder
                .baseUrl(geminiApiUrl)
                .build();
    }

    /**
     * Sends a prompt to Gemini and returns the raw response
     */
    public String getAnswer(String question) {

        Map<String, Object> requestBody = Map.of(
                "contents", new Object[]{
                        Map.of(
                                "role", "user",
                                "parts", new Object[]{
                                        Map.of("text", question)
                                }
                        )
                }
        );

        return webClient.post()
                .uri(uriBuilder -> uriBuilder
                        .queryParam("key", geminiApiKey)
                        .build())
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(requestBody)
                .retrieve()
                .bodyToMono(String.class)
                .block(); // blocking call (OK for now)
    }
}
