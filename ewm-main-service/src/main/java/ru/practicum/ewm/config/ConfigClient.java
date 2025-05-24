package ru.practicum.ewm.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import ru.practicum.stat.StatRestClient;

@Configuration
public class ConfigClient {
    @Bean
    public StatRestClient statRestClient(@Value("${stat-server.url}") String url) {
        return new StatRestClient(url);
    }
}
