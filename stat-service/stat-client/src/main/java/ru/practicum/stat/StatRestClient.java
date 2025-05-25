package ru.practicum.stat;

import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.lang.Nullable;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.DefaultUriBuilderFactory;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class StatRestClient {
    private final RestTemplate rest;

    public StatRestClient(String serverUrl) {
        this.rest = new RestTemplateBuilder()
                .uriTemplateHandler(new DefaultUriBuilderFactory(serverUrl))
                .requestFactory(() -> new HttpComponentsClientHttpRequestFactory())
                .build();
    }

    public ResponseEntity<Void> addHit(EndpointHitDto dto) {
        return rest.exchange("/hit", HttpMethod.POST, new HttpEntity<>(dto), Void.class);
    }

    public ResponseEntity<List<ViewStatsDto>> getStats(LocalDateTime start, LocalDateTime end, @Nullable List<String> uris, @Nullable Boolean unique) {
        Map<String, Object> uriVariables = new HashMap<>();
        uriVariables.put("start", start);
        uriVariables.put("end", end);

        if (uris != null) {
            uriVariables.put("uris", String.join(",", uris));
        }
        if (unique != null) {
            uriVariables.put("unique", unique);
        }

        ParameterizedTypeReference<List<ViewStatsDto>> responseType =
                new ParameterizedTypeReference<>() {
                };

        return rest.exchange(
                "/stats?start={start}&end={end}" +
                (uris != null ? "&uris={uris}" : "") +
                (unique != null ? "&unique={unique}" : ""),
                HttpMethod.GET,
                null,
                responseType,
                uriVariables
        );
    }
}
