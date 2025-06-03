package ru.practicum.ewm.event;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.practicum.ewm.request.RequestStatus;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface EventRepository extends JpaRepository<EventEntity, Long> {
    List<EventEntity> findAllByInitiatorId(Long initiatorId, Pageable pageable);

    @Query("select e from EventEntity e join fetch e.initiator i join fetch e.category c where e.id = :id and i.id = :initiatorId")
    Optional<EventEntity> findAllByInitiatorIdAndId(@Param("initiatorId") Long initiatorId,
                                                    @Param("id") Long id);

    @Query("select e from EventEntity e join fetch e.initiator i join fetch e.category c left join e.requests r " +
           "where (:users is null or i.id in :users) and (:states is null or e.state in :states) and (:categories is null or c.id in :categories) " +
           "and e.eventDate between coalesce(:start, current_timestamp) and coalesce(:end, e.eventDate) " +
           "order by e.id")
    List<EventEntity> findAllByParam(@Param("users") List<Long> users,
                                     @Param("states") List<EventState> states,
                                     @Param("categories") List<Long> categories,
                                     @Param("start") LocalDateTime start,
                                     @Param("end") LocalDateTime end,
                                     Pageable pageable);

    @Query("select e from EventEntity e join fetch e.initiator i join fetch e.category c left join fetch e.requests r where e.id = :id and e.state = :state")
    Optional<EventEntity> findByIdAndState(@Param("id") Long id,
                                           @Param("state") EventState state);


    @Query("select e from EventEntity e join fetch e.initiator i join fetch e.category c left join fetch e.requests r " +
           "where (:text is null or e.annotation ilike %:text% or e.description ilike %:text%) and (:categories is null or c.id in :categories) " +
           "and (:paid is null or e.paid = :paid) " +
           "and ((:onlyAvailable = true and e.participantLimit <= (select count(req.id) from RequestEntity req where req.status = :reqStatus)) or :onlyAvailable = false) " +
           "and e.eventDate between coalesce(:start, current_timestamp) and coalesce(:end, e.eventDate) " +
           "order by e.eventDate asc")
    List<EventEntity> findAllWithRequestsByParam(@Param("text") String text,
                                                 @Param("categories") List<Long> categories,
                                                 @Param("paid") Boolean paid,
                                                 @Param("start") LocalDateTime start,
                                                 @Param("end") LocalDateTime end,
                                                 @Param("onlyAvailable") Boolean onlyAvailable,
                                                 @Param("reqStatus") RequestStatus reqStatus,
                                                 Pageable pageable);

    @Query("select e from EventEntity e join fetch e.initiator i join fetch e.category c left join fetch e.requests r where e.id = :id and i.id = :initiatorId")
    Optional<EventEntity> findByIdAndInitiatorId(@Param("id") Long id,
                                                 @Param("initiatorId") Long initiatorId);
}
