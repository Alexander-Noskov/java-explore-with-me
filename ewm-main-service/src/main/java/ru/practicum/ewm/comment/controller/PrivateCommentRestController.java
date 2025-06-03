package ru.practicum.ewm.comment.controller;

import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewm.comment.CommentMapper;
import ru.practicum.ewm.comment.CommentService;
import ru.practicum.ewm.comment.dto.CommentDto;
import ru.practicum.ewm.comment.dto.NewCommentDto;
import ru.practicum.ewm.comment.dto.UpdateCommentDto;

@RestController
@RequiredArgsConstructor
@RequestMapping("/users/{userId}/events/{eventId}/comments")
@Validated
public class PrivateCommentRestController {
    private final CommentService commentService;
    private final CommentMapper commentMapper;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public CommentDto addComment(@PathVariable @NotNull Long userId,
                                 @PathVariable @NotNull Long eventId,
                                 @NotNull @RequestBody NewCommentDto dto) {
        return commentMapper.toCommentDto(commentService.addComment(userId, eventId, dto.getContent()));
    }

    @PatchMapping("/{commentId}")
    @ResponseStatus(HttpStatus.OK)
    public CommentDto updateComment(@PathVariable @NotNull Long userId,
                                    @PathVariable @NotNull Long eventId,
                                    @PathVariable @NotNull Long commentId,
                                    @NotNull @RequestBody UpdateCommentDto dto) {
        return commentMapper.toCommentDto(commentService.updateComment(userId, eventId, commentId, dto.getContent()));
    }

    @DeleteMapping("/{commentId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteComment(@PathVariable @NotNull Long userId,
                              @PathVariable @NotNull Long eventId,
                              @PathVariable @NotNull Long commentId) {
        commentService.deleteComment(userId, eventId, commentId);
    }

    @GetMapping("/{commentId}")
    @ResponseStatus(HttpStatus.OK)
    public CommentDto getCommentById(@PathVariable @NotNull Long userId,
                                     @PathVariable @NotNull Long eventId,
                                     @PathVariable @NotNull Long commentId) {
        return commentMapper.toCommentDto(commentService.getCommentById(userId, eventId, commentId));
    }
}
