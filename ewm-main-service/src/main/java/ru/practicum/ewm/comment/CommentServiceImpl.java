package ru.practicum.ewm.comment;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.ewm.event.EventEntity;
import ru.practicum.ewm.event.EventService;
import ru.practicum.ewm.event.EventState;
import ru.practicum.ewm.exception.ConflictException;
import ru.practicum.ewm.exception.NotFoundException;
import ru.practicum.ewm.exception.ValidException;
import ru.practicum.ewm.user.UserEntity;
import ru.practicum.ewm.user.UserService;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CommentServiceImpl implements CommentService {
    private final CommentRepository commentRepository;
    private final UserService userService;
    private final EventService eventService;

    @Override
    public CommentEntity addComment(Long userId, Long eventId, String content) {
        UserEntity user = userService.getById(userId);
        EventEntity event = eventService.getEventByIdAndState(eventId, EventState.PUBLISHED);

        CommentEntity comment = CommentEntity.builder()
                .author(user)
                .event(event)
                .content(content)
                .created(LocalDateTime.now())
                .build();

        return commentRepository.save(comment);
    }

    @Override
    public List<CommentEntity> getCommentsByEventId(Long eventId, Integer from, Integer size) {
        return commentRepository.findAllByEventId(eventId, from, size);
    }

    @Override
    public void deleteComment(Long userId, Long eventId, Long commentId) {
        CommentEntity comment = getCommentById(userId, eventId, commentId);
        if (!comment.getAuthor().getId().equals(userId)) {
            throw new ConflictException("Comment with id=" + commentId + " does not belong to this user");
        }
        commentRepository.delete(comment);
    }

    @Override
    public CommentEntity updateComment(Long userId, Long eventId, Long commentId, String content) {
        CommentEntity comment = getCommentById(userId, eventId, commentId);
        comment.setContent(content);
        comment.setUpdated(LocalDateTime.now());
        return commentRepository.save(comment);
    }

    @Override
    public CommentEntity getCommentById(Long userId, Long eventId, Long commentId) {
        return commentRepository.findByIdAndAuthorIdAndEventId(commentId, userId, eventId)
                .orElseThrow(() -> new NotFoundException("Comment with id " + commentId + " not found"));
    }

    @Override
    public void deleteCommentByAdmin(Long commentId) {
        CommentEntity comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new NotFoundException("Comment with id " + commentId + " not found"));
        commentRepository.delete(comment);
    }

    @Override
    public List<CommentEntity> findCommentsByParam(List<Long> userIds, List<Long> eventIds, String text, LocalDateTime rangeStart, LocalDateTime rangeEnd, Integer from, Integer size) {
        if (rangeStart != null && rangeEnd != null && rangeStart.isAfter(rangeEnd)) {
            throw new ValidException("Range start is after range end");
        }

        return commentRepository.findAllByParam(userIds, eventIds, text, rangeStart, rangeEnd, from, size);
    }
}
