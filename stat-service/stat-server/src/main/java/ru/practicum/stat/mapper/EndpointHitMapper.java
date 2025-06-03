package ru.practicum.stat.mapper;

import org.springframework.stereotype.Component;
import ru.practicum.stat.EndpointHitDto;
import ru.practicum.stat.ViewStatsDto;
import ru.practicum.stat.entity.EndpointHitEntity;
import ru.practicum.stat.entity.ViewStatsProjection;

@Component
public final class EndpointHitMapper {
    public EndpointHitEntity toEntity(final EndpointHitDto dto) {
        if (dto == null) {
            return null;
        }
        return EndpointHitEntity.builder()
                .app(dto.getApp())
                .uri(dto.getUri())
                .ip(dto.getIp())
                .timestamp(dto.getTimestamp())
                .build();
    }

    public ViewStatsDto toViewStatsDto(final ViewStatsProjection projection) {
        if (projection == null) {
            return null;
        }
        return new ViewStatsDto(projection.getApp(), projection.getUri(), projection.getHits());
    }
}
