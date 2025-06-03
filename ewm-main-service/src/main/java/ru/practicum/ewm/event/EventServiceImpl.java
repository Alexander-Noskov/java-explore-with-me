package ru.practicum.ewm.event;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import ru.practicum.ewm.category.CategoryEntity;
import ru.practicum.ewm.category.CategoryService;
import ru.practicum.ewm.comment.CommentEntity;
import ru.practicum.ewm.comment.CommentMapper;
import ru.practicum.ewm.comment.CommentRepository;
import ru.practicum.ewm.event.dto.*;
import ru.practicum.ewm.exception.ConflictException;
import ru.practicum.ewm.exception.NotFoundException;
import ru.practicum.ewm.exception.StatServiceException;
import ru.practicum.ewm.exception.ValidException;
import ru.practicum.ewm.request.RequestStatus;
import ru.practicum.ewm.user.UserEntity;
import ru.practicum.ewm.user.UserService;
import ru.practicum.stat.EndpointHitDto;
import ru.practicum.stat.StatRestClient;
import ru.practicum.stat.ViewStatsDto;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class EventServiceImpl implements EventService {
    private final EventRepository eventRepository;
    private final CategoryService categoryService;
    private final CommentRepository commentRepository;
    private final UserService userService;
    private final EventMapper eventMapper;
    private final CommentMapper commentMapper;
    private final StatRestClient statRestClient;

    @Value("${service.name}")
    private String serviceName;

    @Override
    public List<EventShortDto> getEventsByInitiatorIdAndParam(Long initiatorId, Integer from, Integer size) {
        Pageable pageable = PageRequest.of(from / size, size);
        return eventRepository.findAllByInitiatorId(initiatorId, pageable).stream()
                .map(eventMapper::toEventShortDto)
                .toList();
    }

    @Override
    public EventFullDto addEvent(Long userId, NewEventDto newEventDto) {
        EventEntity eventEntity = eventMapper.toEventEntity(newEventDto);
        CategoryEntity category = categoryService.getById(newEventDto.getCategory());
        UserEntity initiator = userService.getById(userId);
        eventEntity.setCategory(category);
        eventEntity.setInitiator(initiator);
        eventEntity.setState(EventState.PENDING);
        eventEntity.setCreatedOn(LocalDateTime.now());
        eventRepository.save(eventEntity);

        return eventMapper.toEventFullDto(eventEntity);
    }

    @Override
    public EventFullDto getEventFullDtoById(Long initiatorId, Long eventId, Integer commentFrom, Integer commentSize) {
        EventEntity eventEntity = eventRepository.findAllByInitiatorIdAndId(initiatorId, eventId)
                .orElseThrow(() -> new NotFoundException("Event with id=" + eventId + " was not found"));

        EventFullDto dto = eventMapper.toEventFullDto(eventEntity);

        return setComments(dto, commentFrom, commentSize);
    }

    @Override
    public List<EventEntity> getAllEventsByIds(List<Long> eventIds) {
        List<EventEntity> events = eventRepository.findAllById(eventIds);

        if (eventIds.size() == events.size()) {
            return events;
        }

        List<Long> foundIds = events.stream().map(EventEntity::getId).toList();

        List<Long> missingIds = eventIds.stream().filter(id -> !foundIds.contains(id)).toList();

        throw new NotFoundException("Events with ids=" + missingIds + " was not found");
    }

    @Override
    public EventEntity getEventById(Long eventId) {
        return eventRepository.findById(eventId)
                .orElseThrow(() -> new NotFoundException("Event with id=" + eventId + " was not found"));
    }

    @Override
    public EventEntity getEventByIdAndState(Long eventId, EventState state) {
        return eventRepository.findByIdAndState(eventId, state)
                .orElseThrow(() -> new NotFoundException("Event with id=" + eventId + " was not found"));
    }

    @Override
    public List<EventFullDto> getEventsPyParam(List<Long> users, List<EventState> states, List<Long> categories, LocalDateTime rangeStart, LocalDateTime rangeEnd, Integer from, Integer size) {
        Pageable pageable = PageRequest.of(from / size, size);
        List<EventEntity> entities = eventRepository.findAllByParam(users, states, categories, rangeStart, rangeEnd, pageable);
        return entities.stream().map(this::mapToFullDtoAndSetRequests).toList();
    }

    @Override
    public EventFullDto updateEventByAdmin(Long eventId, UpdateEventAdminRequestDto dto) {
        EventEntity event = getEventById(eventId);

        if (dto.getAnnotation() != null) {
            event.setAnnotation(dto.getAnnotation());
        }
        if (dto.getCategory() != null) {
            event.setCategory(categoryService.getById(dto.getCategory()));
        }
        if (dto.getDescription() != null) {
            event.setDescription(dto.getDescription());
        }
        if (dto.getEventDate() != null) {
            event.setEventDate(dto.getEventDate());
        }
        if (dto.getLocation() != null && dto.getLocation().getLat() != null && dto.getLocation().getLon() != null) {
            event.setLat(dto.getLocation().getLat());
            event.setLon(dto.getLocation().getLon());
        }
        if (dto.getPaid() != null) {
            event.setPaid(dto.getPaid());
        }
        if (dto.getParticipantLimit() != null) {
            event.setParticipantLimit(dto.getParticipantLimit());
        }
        if (dto.getRequestModeration() != null) {
            event.setRequestModeration(dto.getRequestModeration());
        }
        if (dto.getStateAction() != null) {
            if (dto.getStateAction().equals(StateAdminAction.PUBLISH_EVENT)) {
                if (event.getEventDate().isBefore(LocalDateTime.now().plusHours(1))) {
                    throw new ConflictException("Дата начала изменяемого события должна быть не ранее чем за час от даты публикации");
                }
                if (!event.getState().equals(EventState.PENDING)) {
                    throw new ConflictException("Событие можно публиковать, только если оно в состоянии ожидания публикации");
                }
                event.setState(EventState.PUBLISHED);
                event.setPublishedOn(LocalDateTime.now());
            } else if (dto.getStateAction().equals(StateAdminAction.REJECT_EVENT)) {
                if (event.getState().equals(EventState.PUBLISHED)) {
                    throw new ConflictException("Событие можно отклонить, только если оно еще не опубликовано");
                }
                event.setState(EventState.CANCELED);
            }
        }
        if (dto.getTitle() != null) {
            event.setTitle(dto.getTitle());
        }

        return eventMapper.toEventFullDto(eventRepository.save(event));
    }

    @Override
    public EventEntity getByIdAndInitiatorId(Long eventId, Long initiatorId) {
        return eventRepository.findByIdAndInitiatorId(eventId, initiatorId)
                .orElseThrow(() -> new NotFoundException("Event with id=" + eventId + " was not found"));
    }

    @Override
    public EventFullDto updateEventByUser(Long userId, Long eventId, UpdateEventUserRequestDto dto) {
        EventEntity event = getByIdAndInitiatorId(eventId, userId);

        if (!event.getState().equals(EventState.PENDING) && !event.getState().equals(EventState.CANCELED)) {
            throw new ConflictException("Изменить можно только отмененные события или события в состоянии ожидания модерации");
        }

        if (dto.getAnnotation() != null) {
            event.setAnnotation(dto.getAnnotation());
        }
        if (dto.getCategory() != null) {
            event.setCategory(categoryService.getById(dto.getCategory()));
        }
        if (dto.getDescription() != null) {
            event.setDescription(dto.getDescription());
        }
        if (dto.getEventDate() != null) {
            event.setEventDate(dto.getEventDate());
        }
        if (dto.getLocation() != null && dto.getLocation().getLat() != null && dto.getLocation().getLon() != null) {
            event.setLat(dto.getLocation().getLat());
            event.setLon(dto.getLocation().getLon());
        }
        if (dto.getPaid() != null) {
            event.setPaid(dto.getPaid());
        }
        if (dto.getParticipantLimit() != null) {
            event.setParticipantLimit(dto.getParticipantLimit());
        }
        if (dto.getRequestModeration() != null) {
            event.setRequestModeration(dto.getRequestModeration());
        }
        if (dto.getStateAction() != null) {
            if (dto.getStateAction().equals(StateUserAction.SEND_TO_REVIEW)) {
                event.setState(EventState.PENDING);
            } else if (dto.getStateAction().equals(StateUserAction.CANCEL_REVIEW)) {
                event.setState(EventState.CANCELED);
            }
        }
        if (dto.getTitle() != null) {
            event.setTitle(dto.getTitle());
        }

        return eventMapper.toEventFullDto(eventRepository.save(event));
    }

    @Override
    public EventFullDto getPublicEventById(Long eventId, String remoteAddr, String requestUri, Integer commentFrom, Integer commentSize) {
        EventEntity event = getEventByIdAndState(eventId, EventState.PUBLISHED);

        statRestClient.addHit(new EndpointHitDto(serviceName, requestUri, remoteAddr, LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS)));

        EventFullDto dto = setViewsInFullDto(mapToFullDtoAndSetRequests(event), event.getCreatedOn().truncatedTo(ChronoUnit.SECONDS), requestUri);

        return setComments(dto, commentFrom, commentSize);
    }

    @Override
    public List<EventShortDto> getPublicEvents(String remoteAddr, String requestURI, String text, List<Long> categories, Boolean paid, LocalDateTime rangeStart, LocalDateTime rangeEnd, Boolean onlyAvailable, SortValue sort, Integer from, Integer size) {
        if (rangeEnd != null && rangeStart != null && rangeEnd.isBefore(rangeStart)) {
            throw new ValidException("rangeStart должна быть раньше rangeEnd");
        }
        Pageable pageable = PageRequest.of(from / size, size);
        List<EventEntity> events = eventRepository.findAllWithRequestsByParam(text, categories, paid, rangeStart, rangeEnd, onlyAvailable, RequestStatus.CONFIRMED, pageable);
        List<EventShortDto> shortEvents = events.stream()
                .map(this::mapToShortDtoAndSetRequests)
                .toList();
        LocalDateTime start = events.stream()
                .map(EventEntity::getCreatedOn)
                .min(LocalDateTime::compareTo)
                .orElse(LocalDateTime.now());

        statRestClient.addHit(new EndpointHitDto(serviceName, requestURI, remoteAddr, LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS)));

        List<EventShortDto> eventsWithRequestsAndViews = setViews(shortEvents, start.truncatedTo(ChronoUnit.SECONDS));

        if (sort != null && sort.equals(SortValue.VIEWS)) {
            return eventsWithRequestsAndViews.stream()
                    .sorted(Comparator.comparing(EventShortDto::getViews).reversed())
                    .toList();
        }
        return eventsWithRequestsAndViews;
    }

    private EventFullDto setComments(EventFullDto dto, Integer from, Integer size) {
        List<CommentEntity> comments = commentRepository.findAllByEventId(dto.getId(), from, size);
        dto.setComments(comments.stream().map(commentMapper::toCommentDto).toList());
        return dto;
    }

    private EventFullDto mapToFullDtoAndSetRequests(EventEntity event) {
        EventFullDto dto = eventMapper.toEventFullDto(event);
        if (event.getRequests() != null) {
            dto.setConfirmedRequests(event.getRequests().stream()
                    .filter(r -> r.getStatus().equals(RequestStatus.CONFIRMED))
                    .count());
        }
        return dto;
    }

    private EventShortDto mapToShortDtoAndSetRequests(EventEntity event) {
        EventShortDto dto = eventMapper.toEventShortDto(event);
        if (event.getRequests() != null) {
            dto.setConfirmedRequests(event.getRequests().stream()
                    .filter(r -> r.getStatus().equals(RequestStatus.CONFIRMED))
                    .count());
        }
        return dto;
    }

    private EventFullDto setViewsInFullDto(EventFullDto dto, LocalDateTime start, String uri) {
        ResponseEntity<List<ViewStatsDto>> views = statRestClient.getStats(start, LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS), List.of(uri), true);
        if (views.getStatusCode().is2xxSuccessful()) {
            List<ViewStatsDto> viewStats = views.getBody();
            if (viewStats != null && !viewStats.isEmpty()) {
                dto.setViews(viewStats.getFirst().getHits());
            } else {
                dto.setViews(0L);
            }
            return dto;
        }

        throw new StatServiceException("Stat Service ERROR");
    }

    private List<EventShortDto> setViews(List<EventShortDto> events, LocalDateTime start) {
        List<String> uris = events.stream()
                .map(e -> "/events/" + e.getId())
                .toList();
        ResponseEntity<List<ViewStatsDto>> views = statRestClient.getStats(start, LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS), uris, false);
        if (views.getStatusCode().is2xxSuccessful()) {
            List<ViewStatsDto> viewStats = views.getBody();
            if (viewStats != null && !viewStats.isEmpty()) {
                Map<Long, Long> idHits = viewStats.stream()
                        .filter(dto -> dto.getUri() != null)
                        .collect(Collectors.toMap(
                                dto -> extractIdFromUri(dto.getUri()),
                                ViewStatsDto::getHits,
                                (existing, replacement) -> existing
                        ));
                return events.stream()
                        .peek(e -> e.setViews(Optional.ofNullable(idHits.get(e.getId())).orElse(0L)))
                        .toList();
            }
            return events.stream()
                    .peek(e -> e.setViews(0L))
                    .toList();
        }

        throw new StatServiceException("Stat Service ERROR");
    }

    private static Long extractIdFromUri(String uri) {
        try {
            String[] parts = uri.split("/");
            return Long.parseLong(parts[parts.length - 1]);
        } catch (Exception e) {
            throw new IllegalArgumentException("Invalid URI format: " + uri);
        }
    }
}
