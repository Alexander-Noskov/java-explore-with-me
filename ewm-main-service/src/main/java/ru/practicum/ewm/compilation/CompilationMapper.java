package ru.practicum.ewm.compilation;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.practicum.ewm.compilation.dto.CompilationDto;
import ru.practicum.ewm.compilation.dto.NewCompilationDto;
import ru.practicum.ewm.event.EventMapper;

import java.util.ArrayList;

@Component
@RequiredArgsConstructor
public final class CompilationMapper {
    private final EventMapper eventMapper;

    public CompilationEntity toEntity(NewCompilationDto dto) {
        if (dto == null) {
            return null;
        }
        return CompilationEntity.builder()
                .pinned(dto.getPinned())
                .title(dto.getTitle())
                .build();
    }

    public CompilationDto toCompilationDto(CompilationEntity entity) {
        if (entity == null) {
            return null;
        }
        CompilationDto dto = CompilationDto.builder()
                .id(entity.getId())
                .pinned(entity.getPinned())
                .title(entity.getTitle())
                .build();
        if (entity.getEvents() != null && !entity.getEvents().isEmpty()) {
            dto.setEvents(entity.getEvents().stream().map(eventMapper::toEventShortDto).toList());
        } else {
            dto.setEvents(new ArrayList<>());
        }
        return dto;
    }
}
