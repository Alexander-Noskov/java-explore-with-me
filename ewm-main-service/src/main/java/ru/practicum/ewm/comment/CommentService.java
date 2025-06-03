package ru.practicum.ewm.comment;

import java.time.LocalDateTime;
import java.util.List;

public interface CommentService {
    CommentEntity addComment(Long userId, Long eventId, String content);

    List<CommentEntity> getCommentsByEventId(Long eventId, Integer from, Integer size);

    CommentEntity getCommentById(Long userId, Long eventId, Long commentId);

    void deleteComment(Long userId, Long eventId, Long commentId);

    CommentEntity updateComment(Long userId, Long eventId, Long commentId, String content);

    void deleteCommentByAdmin(Long commentId);

    List<CommentEntity> findCommentsByParam(List<Long> userIds, List<Long> eventIds, String text, LocalDateTime rangeStart, LocalDateTime rangeEnd, Integer from, Integer size);
}
