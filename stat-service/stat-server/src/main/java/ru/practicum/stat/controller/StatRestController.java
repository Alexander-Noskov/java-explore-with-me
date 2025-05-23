package ru.practicum.stat.controller;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.stat.EndpointHitDto;
import ru.practicum.stat.ViewStatsDto;
import ru.practicum.stat.mapper.EndpointHitMapper;
import ru.practicum.stat.service.StatService;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping
@Validated
public class StatRestController {
    private final StatService statService;
    private final EndpointHitMapper mapper;

    @PostMapping("/hit")
    @ResponseStatus(HttpStatus.CREATED)
    public void addHit(@RequestBody @Valid EndpointHitDto dto) {
        log.info("Сохранение посещения: {}", dto.toString());
        statService.addHit(mapper.toEntity(dto));
    }

    @GetMapping("/stats")
    @ResponseStatus(HttpStatus.OK)
    public List<ViewStatsDto> getStats(
            @RequestParam @NotNull @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime start,
            @RequestParam @NotNull @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime end,
            @RequestParam(required = false) List<String> uris,
            @RequestParam(defaultValue = "false") Boolean unique) {
        log.info("Запрос статистики по датам: {} - {}", start, end);
        return statService.getStats(start, end, uris, unique).stream()
                .map(mapper::toViewStatsDto)
                .toList();
    }
}
