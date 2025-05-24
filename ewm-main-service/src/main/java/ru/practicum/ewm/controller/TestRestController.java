package ru.practicum.ewm.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.practicum.stat.EndpointHitDto;
import ru.practicum.stat.StatRestClient;
import ru.practicum.stat.ViewStatsDto;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@RestController
@RequestMapping
@RequiredArgsConstructor
public class TestRestController {
    private final StatRestClient statRestClient;

    @PostMapping("/hit")
    public ResponseEntity<Void> addHit(@RequestBody EndpointHitDto dto) {
        log.info("Adding hit {}", dto);
        return statRestClient.addHit(dto);
    }

    @GetMapping("/stats")
    public ResponseEntity<List<ViewStatsDto>> getStats(
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime start,
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime end,
            @RequestParam(required = false) List<String> uris,
            @RequestParam(defaultValue = "false") Boolean unique) {
        log.info("getStats");
        return statRestClient.getStats(start, end, uris, unique);
    }
}
