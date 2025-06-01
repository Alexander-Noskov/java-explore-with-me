package ru.practicum.ewm.event;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.practicum.ewm.category.CategoryMapper;
import ru.practicum.ewm.event.dto.EventFullDto;
import ru.practicum.ewm.event.dto.EventShortDto;
import ru.practicum.ewm.event.dto.Location;
import ru.practicum.ewm.event.dto.NewEventDto;
import ru.practicum.ewm.user.UserMapper;

@Component
@RequiredArgsConstructor
public final class EventMapper {
    private final UserMapper userMapper;
    private final CategoryMapper categoryMapper;

    public EventEntity toEventEntity(NewEventDto dto) {
        if (dto == null) {
            return null;
        }
        return EventEntity.builder()
                .title(dto.getTitle())
                .annotation(dto.getAnnotation())
                .description(dto.getDescription())
                .eventDate(dto.getEventDate())
                .lat(dto.getLocation().getLat())
                .lon(dto.getLocation().getLon())
                .participantLimit(dto.getParticipantLimit())
                .paid(dto.getPaid())
                .requestModeration(dto.getRequestModeration())
                .build();
    }

    public EventFullDto toEventFullDto(EventEntity entity) {
        if (entity == null) {
            return null;
        }

        return EventFullDto.builder()
                .id(entity.getId())
                .title(entity.getTitle())
                .annotation(entity.getAnnotation())
                .description(entity.getDescription())
                .category(categoryMapper.toCategoryDto(entity.getCategory()))
                .initiator(userMapper.toUserShortDto(entity.getInitiator()))
                .location(Location.builder().lat(entity.getLat()).lon(entity.getLon()).build())
                .createdOn(entity.getCreatedOn())
                .eventDate(entity.getEventDate())
                .paid(entity.getPaid())
                .participantLimit(entity.getParticipantLimit())
                .requestModeration(entity.getRequestModeration())
                .state(entity.getState())
                .publishedOn(entity.getPublishedOn())
                .build();
    }

    public EventShortDto toEventShortDto(EventEntity entity) {
        if (entity == null) {
            return null;
        }

        return EventShortDto.builder()
                .id(entity.getId())
                .title(entity.getTitle())
                .annotation(entity.getAnnotation())
                .category(categoryMapper.toCategoryDto(entity.getCategory()))
                .initiator(userMapper.toUserShortDto(entity.getInitiator()))
                .eventDate(entity.getEventDate())
                .paid(entity.getPaid())
                .build();
    }
}
