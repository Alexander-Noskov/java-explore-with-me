package ru.practicum.stat.service;

import ru.practicum.stat.entity.EndpointHitEntity;
import ru.practicum.stat.entity.ViewStatsProjection;

import java.time.LocalDateTime;
import java.util.List;

public interface StatService {
    void addHit(EndpointHitEntity hit);

    List<ViewStatsProjection> getStats(LocalDateTime start, LocalDateTime end, List<String> uris, Boolean unique);
}
