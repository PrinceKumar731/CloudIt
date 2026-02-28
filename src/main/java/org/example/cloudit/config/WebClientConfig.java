package org.example.cloudit.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class WebClientConfig {

    @Value("${mastodon.token}")
    private String mastodonToken;

    @Bean
    public WebClient webClient() {
        return WebClient.builder()
                .baseUrl("https://mastodon.social")
                .defaultHeader("Authorization", "Bearer " + mastodonToken)
                .build();
    }
}
