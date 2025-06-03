package ru.practicum.ewm.user;

import org.springframework.stereotype.Component;
import ru.practicum.ewm.user.dto.NewUserRequestDto;
import ru.practicum.ewm.user.dto.UserDto;
import ru.practicum.ewm.user.dto.UserShortDto;

@Component
public final class UserMapper {
    public UserEntity toUserEntity(final NewUserRequestDto dto) {
        if (dto == null) {
            return null;
        }
        return UserEntity.builder()
                .email(dto.getEmail())
                .name(dto.getName())
                .build();
    }

    public UserDto toUserDto(final UserEntity entity) {
        if (entity == null) {
            return null;
        }
        return UserDto.builder()
                .id(entity.getId())
                .email(entity.getEmail())
                .name(entity.getName())
                .build();
    }

    public UserShortDto toUserShortDto(final UserEntity entity) {
        if (entity == null) {
            return null;
        }
        return UserShortDto.builder()
                .id(entity.getId())
                .name(entity.getName())
                .build();
    }
}
