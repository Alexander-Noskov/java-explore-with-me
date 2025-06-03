package ru.practicum.ewm.request;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface RequestRepository extends JpaRepository<RequestEntity, Long> {
    @Query("select r from RequestEntity r left join r.requester req left join fetch r.event e left join fetch e.initiator i where i.id = :initiatorId and e.id = :eventId")
    List<RequestEntity> findAllByInitiatorIdAndEventId(@Param("initiatorId") Long initiatorId,
                                                       @Param("eventId") Long eventId);

    @Query("select r from RequestEntity r left join r.requester req left join fetch r.event e where req.id = :requesterId")
    List<RequestEntity> findAllByRequesterId(@Param("requesterId") Long requesterId);

    @Query("select r from RequestEntity r left join r.requester req left join fetch r.event e where r.id = :id and req.id = :requesterId")
    Optional<RequestEntity> findByIdAndRequesterId(@Param("id") Long id,
                                                   @Param("requesterId") Long requesterId);

    long countByEventIdAndStatus(Long eventId, RequestStatus status);
}
