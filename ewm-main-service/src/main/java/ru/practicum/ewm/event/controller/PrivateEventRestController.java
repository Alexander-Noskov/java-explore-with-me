package ru.practicum.ewm.event.controller;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewm.event.EventService;
import ru.practicum.ewm.event.dto.EventFullDto;
import ru.practicum.ewm.event.dto.EventShortDto;
import ru.practicum.ewm.event.dto.NewEventDto;
import ru.practicum.ewm.event.dto.UpdateEventUserRequestDto;
import ru.practicum.ewm.request.RequestMapper;
import ru.practicum.ewm.request.RequestService;
import ru.practicum.ewm.request.dto.ParticipationRequestDto;
import ru.practicum.ewm.request.dto.StatusUpdateRequestDto;
import ru.practicum.ewm.request.dto.StatusUpdateResultDto;

import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/users/{userId}/events")
@Validated
public class PrivateEventRestController {
    private final EventService eventService;
    private final RequestService requestService;
    private final RequestMapper requestMapper;

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<EventShortDto> getAllEvents(@PathVariable @NotNull Long userId,
                                            @RequestParam(required = false, defaultValue = "0") @PositiveOrZero Integer from,
                                            @RequestParam(required = false, defaultValue = "10") @Positive Integer size) {
        log.info("Getting all events for user: {}, from: {}, size: {}", userId, from, size);
        return eventService.getEventsByInitiatorIdAndParam(userId, from, size);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public EventFullDto addEvent(@PathVariable @NotNull Long userId, @RequestBody @Valid NewEventDto dto) {
        log.info("Adding new event: {}, userId: {}", dto, userId);
        return eventService.addEvent(userId, dto);
    }

    @GetMapping("/{eventId}")
    @ResponseStatus(HttpStatus.OK)
    public EventFullDto getEventById(@PathVariable @NotNull Long userId, @PathVariable @NotNull Long eventId) {
        log.info("Getting event by id: {}, userId: {}", eventId, userId);
        return eventService.getEventFullDtoById(userId, eventId);
    }

    @PatchMapping("/{eventId}")
    @ResponseStatus(HttpStatus.OK)
    public EventFullDto updateEventUserRequest(@PathVariable @NotNull Long userId,
                                               @PathVariable @NotNull Long eventId,
                                               @RequestBody @Valid UpdateEventUserRequestDto dto) {
        log.info("Updating event user request: {}, eventId: {}, userId: {}", dto, eventId, userId);
        return eventService.updateEventByUser(userId, eventId, dto);
    }

    @GetMapping("/{eventId}/requests")
    @ResponseStatus(HttpStatus.OK)
    public List<ParticipationRequestDto> getRequestByEventId(@PathVariable @NotNull Long userId, @PathVariable @NotNull Long eventId) {
        log.info("Get requests by event id: {}, user id: {}", eventId, userId);
        return requestService.getRequests(userId, eventId).stream()
                .map(requestMapper::toParticipationRequestDto)
                .toList();
    }

    @PatchMapping("/{eventId}/requests")
    @ResponseStatus(HttpStatus.OK)
    public StatusUpdateResultDto updateStatusRequests(@PathVariable @NotNull Long userId,
                                                      @PathVariable @NotNull Long eventId,
                                                      @RequestBody @Valid StatusUpdateRequestDto dto) {
        log.info("Updating request status for eventId: {}, userId: {}, dto: {}", eventId, userId, dto);
        return requestMapper.toStatusUpdateResultDto(requestService.updateStatusRequests(userId, eventId, dto.getRequestIds(), dto.getStatus()));
    }
}
