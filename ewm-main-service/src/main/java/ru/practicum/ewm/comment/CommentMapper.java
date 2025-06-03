package ru.practicum.ewm.comment;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.practicum.ewm.comment.dto.CommentDto;
import ru.practicum.ewm.user.UserMapper;

@Component
@RequiredArgsConstructor
public final class CommentMapper {
    private final UserMapper userMapper;

    public CommentDto toCommentDto(CommentEntity commentEntity) {
        if (commentEntity == null) {
            return null;
        }
        return CommentDto.builder()
                .id(commentEntity.getId())
                .content(commentEntity.getContent())
                .createdAt(commentEntity.getCreated())
                .updatedAt(commentEntity.getUpdated())
                .author(userMapper.toUserShortDto(commentEntity.getAuthor()))
                .build();
    }
}
