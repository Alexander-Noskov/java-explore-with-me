package ru.practicum.ewm.event;

import ru.practicum.ewm.event.dto.*;

import java.time.LocalDateTime;
import java.util.List;

public interface EventService {
    EventFullDto addEvent(Long userId, NewEventDto newEventDto);

    List<EventEntity> getAllEventsByIds(List<Long> eventIds);

    EventEntity getEventById(Long eventId);

    EventEntity getEventByIdAndState(Long eventId, EventState state);

    EventEntity getByIdAndInitiatorId(Long eventId, Long initiatorId);

    List<EventShortDto> getEventsByInitiatorIdAndParam(Long initiatorId, Integer from, Integer size);

    EventFullDto getEventFullDtoById(Long initiatorId, Long eventId, Integer commentFrom, Integer commentSize);

    List<EventFullDto> getEventsPyParam(List<Long> users, List<EventState> states, List<Long> categories, LocalDateTime rangeStart, LocalDateTime rangeEnd, Integer from, Integer size);

    EventFullDto updateEventByAdmin(Long eventId, UpdateEventAdminRequestDto dto);

    EventFullDto getPublicEventById(Long eventId, String remoteAddr, String requestUri, Integer commentFrom, Integer commentSize);

    List<EventShortDto> getPublicEvents(String remoteAddr, String requestURI, String text, List<Long> categories, Boolean paid, LocalDateTime rangeStart, LocalDateTime rangeEnd, Boolean onlyAvailable, SortValue sort, Integer from, Integer size);

    EventFullDto updateEventByUser(Long userId, Long eventId, UpdateEventUserRequestDto dto);
}
