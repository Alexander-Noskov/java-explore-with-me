package ru.practicum.stat.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.stat.entity.EndpointHitEntity;
import ru.practicum.stat.entity.ViewStatsProjection;
import ru.practicum.stat.repository.EndpointHitRepository;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class StatServiceImpl implements StatService {
    private final EndpointHitRepository endpointHitRepository;

    @Override
    public void addHit(EndpointHitEntity hit) {
        endpointHitRepository.save(hit);
    }

    @Override
    public List<ViewStatsProjection> getStats(LocalDateTime start, LocalDateTime end, List<String> uris, Boolean unique) {
        return endpointHitRepository.findStatsProjection(Timestamp.valueOf(start), Timestamp.valueOf(end), uris, unique);
    }
}
