package ru.practicum.ewm.request.controller;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewm.request.RequestMapper;
import ru.practicum.ewm.request.RequestService;
import ru.practicum.ewm.request.dto.ParticipationRequestDto;

import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/users/{userId}/requests")
public class PrivateRequestRestController {
    private final RequestService requestService;
    private final RequestMapper requestMapper;

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<ParticipationRequestDto> getRequests(@PathVariable @NotNull @Positive Long userId) {
        log.info("Get requests by userId: {}", userId);
        return requestService.getRequestsByUserId(userId).stream()
                .map(requestMapper::toParticipationRequestDto)
                .toList();
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ParticipationRequestDto addRequest(@PathVariable @NotNull @Positive Long userId, @RequestParam @NotNull @Positive Long eventId) {
        log.info("Add request userId: {}, eventId: {}", userId, eventId);
        return requestMapper.toParticipationRequestDto(requestService.addRequest(userId, eventId));
    }

    @PatchMapping("{requestId}/cancel")
    @ResponseStatus(HttpStatus.OK)
    public ParticipationRequestDto cancelRequest(@PathVariable @NotNull @Positive Long userId,
                                                 @PathVariable @NotNull @Positive Long requestId) {
        log.info("Cancel request userId: {}, requestId: {}", userId, requestId);
        return requestMapper.toParticipationRequestDto(requestService.cancelRequest(userId, requestId));
    }
}
