package ru.practicum.ewm.request;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.ewm.event.EventEntity;
import ru.practicum.ewm.event.EventService;
import ru.practicum.ewm.event.EventState;
import ru.practicum.ewm.exception.ConflictException;
import ru.practicum.ewm.exception.NotFoundException;
import ru.practicum.ewm.user.UserEntity;
import ru.practicum.ewm.user.UserService;

import java.sql.Timestamp;
import java.time.Instant;
import java.util.List;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
public class RequestServiceImpl implements RequestService {
    private final RequestRepository requestRepository;
    private final UserService userService;
    private final EventService eventService;

    @Override
    public RequestEntity addRequest(Long userId, Long eventId) {
        UserEntity user = userService.getById(userId);
        EventEntity event = eventService.getEventById(eventId);
        if (event.getInitiator().equals(user)) {
            throw new ConflictException("Инициатор события не может добавить запрос на участие в своём событии");
        }
        if (!event.getState().equals(EventState.PUBLISHED)) {
            throw new ConflictException("Нельзя участвовать в неопубликованном событии");
        }

        if (isLimit(event)) {
            throw new ConflictException("У события достигнут лимит участников");
        }

        RequestEntity request = RequestEntity.builder()
                .requester(user)
                .event(event)
                .created(Timestamp.from(Instant.now()))
                .build();

        if (!event.getRequestModeration() || event.getParticipantLimit() == 0) {
            request.setStatus(RequestStatus.CONFIRMED);
        } else {
            request.setStatus(RequestStatus.PENDING);
        }
        return requestRepository.save(request);
    }

    @Override
    public List<RequestEntity> getRequests(Long userId, Long eventId) {
        return requestRepository.findAllByInitiatorIdAndEventId(userId, eventId);
    }

    @Override
    public List<RequestEntity> getRequestsByUserId(Long userId) {
        userService.getById(userId);
        return requestRepository.findAllByRequesterId(userId);
    }

    @Override
    public RequestEntity cancelRequest(Long userId, Long requestId) {
        userService.getById(userId);
        RequestEntity request = requestRepository.findByIdAndRequesterId(requestId, userId)
                .orElseThrow(() -> new NotFoundException("Request with id=" + requestId + " was not found"));
        request.setStatus(RequestStatus.CANCELED);
        return requestRepository.save(request);
    }

    private boolean isLimit(EventEntity event) {
        return event.getParticipantLimit() != 0 && event.getParticipantLimit() == requestRepository.countByEventIdAndStatus(event.getId(), RequestStatus.CONFIRMED);
    }

    @Override
    @Transactional
    public List<RequestEntity> updateStatusRequests(Long userId, Long eventId, List<Long> requestIds, UpdateRequestStatus status) {
        EventEntity event = eventService.getByIdAndInitiatorId(eventId, userId);

        if (status.equals(UpdateRequestStatus.CONFIRMED)) {
            if (isLimit(event)) {
                throw new ConflictException("Нельзя подтвердить заявку, если уже достигнут лимит по заявкам на данное событие");
            }
        }

        List<RequestEntity> requestsForUpdate = event.getRequests().stream()
                .filter(req -> requestIds.contains(req.getId()))
                .peek(req -> {
                    if (!req.getStatus().equals(RequestStatus.PENDING)) {
                        throw new ConflictException("Статус можно изменить только у заявок, находящихся в состоянии ожидания");
                    }
                    if (status.equals(UpdateRequestStatus.CONFIRMED) && !isLimit(event)) {
                        req.setStatus(RequestStatus.CONFIRMED);
                    } else {
                        req.setStatus(RequestStatus.REJECTED);
                    }
                })
                .toList();

        requestRepository.saveAll(requestsForUpdate);
        if (isLimit(event)) {
            List<RequestEntity> reqs = event.getRequests().stream()
                    .filter(req -> !requestIds.contains(req.getId()))
                    .filter(req -> req.getStatus().equals(RequestStatus.PENDING))
                    .peek(req -> req.setStatus(RequestStatus.REJECTED))
                    .toList();
            requestRepository.saveAll(reqs);
            return Stream.concat(requestsForUpdate.stream(), reqs.stream()).toList();
        }
        return requestsForUpdate;
    }
}
