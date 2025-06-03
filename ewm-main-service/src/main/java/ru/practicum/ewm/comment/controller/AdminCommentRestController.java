package ru.practicum.ewm.comment.controller;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewm.comment.CommentMapper;
import ru.practicum.ewm.comment.CommentService;
import ru.practicum.ewm.comment.dto.CommentDto;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/comments")
@Validated
public class AdminCommentRestController {
    private final CommentService commentService;
    private final CommentMapper commentMapper;

    @DeleteMapping("/{commentId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteComment(@PathVariable @NotNull Long commentId) {
        commentService.deleteCommentByAdmin(commentId);
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<CommentDto> findCommentsByParam(@RequestParam(required = false) List<Long> users,
                                                @RequestParam(required = false) List<Long> events,
                                                @RequestParam(required = false) String text,
                                                @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime rangeStart,
                                                @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime rangeEnd,
                                                @RequestParam(required = false, defaultValue = "0") @PositiveOrZero Integer from,
                                                @RequestParam(required = false, defaultValue = "10") @Positive Integer size) {
        return commentService.findCommentsByParam(users, events, text, rangeStart, rangeEnd, from, size).stream()
                .map(commentMapper::toCommentDto)
                .toList();
    }
}
