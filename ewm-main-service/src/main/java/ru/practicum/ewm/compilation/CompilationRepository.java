package ru.practicum.ewm.compilation;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface CompilationRepository extends JpaRepository<CompilationEntity, Long> {

    @Query("select c from CompilationEntity c left join fetch c.events e left join fetch e.category cat left join fetch e.initiator i where (:pinned is null or c.pinned = :pinned) order by c.id")
    List<CompilationEntity> getCompilationsByParam(
            @Param("pinned") Boolean pinned,
            Pageable pageable);
}
