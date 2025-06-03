package ru.practicum.stat.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.practicum.stat.entity.EndpointHitEntity;
import ru.practicum.stat.entity.ViewStatsProjection;

import java.time.LocalDateTime;
import java.util.List;

public interface EndpointHitRepository extends JpaRepository<EndpointHitEntity, Long> {
    @Query("SELECT e.app AS app, e.uri AS uri, CASE WHEN :unique = true THEN COUNT(DISTINCT e.ip) ELSE COUNT(e.ip) END AS hits " +
           "FROM EndpointHitEntity e " +
           "WHERE e.timestamp BETWEEN :start AND :end " +
           "AND (:uris IS NULL OR e.uri IN :uris) " +
           "GROUP BY e.app, e.uri " +
           "ORDER BY hits DESC")
    List<ViewStatsProjection> findStatsProjection(
            @Param("start") LocalDateTime start,
            @Param("end") LocalDateTime end,
            @Param("uris") List<String> uris,
            @Param("unique") Boolean unique
    );
}
