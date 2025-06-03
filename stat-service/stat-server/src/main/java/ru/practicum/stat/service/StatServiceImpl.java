package ru.practicum.stat.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.stat.entity.EndpointHitEntity;
import ru.practicum.stat.entity.ViewStatsProjection;
import ru.practicum.stat.exception.ValidException;
import ru.practicum.stat.repository.EndpointHitRepository;

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
        if (start.isAfter(end)) {
            throw new ValidException("Start date cannot be after end date");
        }
        return endpointHitRepository.findStatsProjection(start, end, uris, unique);
    }
}
