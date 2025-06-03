package ru.practicum.ewm.comment;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface CommentRepository extends JpaRepository<CommentEntity, Long> {

    @Query("select c from CommentEntity c join fetch c.author a join fetch c.event e where e.id = :eventId order by c.id limit :size offset :from")
    List<CommentEntity> findAllByEventId(@Param("eventId") Long eventId,
                                         @Param("from") Integer from,
                                         @Param("size") Integer size);

    @Query("select c from CommentEntity c join fetch c.author a join fetch c.event e where c.id = :commentId and a.id = :authorId and e.id = :eventId")
    Optional<CommentEntity> findByIdAndAuthorIdAndEventId(@Param("commentId") Long commentId,
                                                          @Param("authorId") Long authorId,
                                                          @Param("eventId") Long eventId);

    @Query("select c from CommentEntity c join fetch c.author a join fetch c.event e where (:users is null or a.id in :users) " +
           "and (:events is null or e.id in :events) and (:text is null or c.content ilike %:text%) " +
           "and c.created between coalesce(:start, c.created) and coalesce(:end, c.created) " +
           "order by c.created asc limit :size offset :from")
    List<CommentEntity> findAllByParam(@Param("users") List<Long> userIds,
                                       @Param("events") List<Long> eventIds,
                                       @Param("text") String text,
                                       @Param("start") LocalDateTime rangeStart,
                                       @Param("end") LocalDateTime rangeEnd,
                                       @Param("from") Integer from,
                                       @Param("size") Integer size);
}
