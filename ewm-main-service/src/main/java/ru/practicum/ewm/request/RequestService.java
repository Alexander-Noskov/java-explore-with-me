package ru.practicum.ewm.request;

import java.util.List;

public interface RequestService {
    RequestEntity addRequest(Long userId, Long eventId);

    List<RequestEntity> getRequests(Long userId, Long eventId);

    List<RequestEntity> getRequestsByUserId(Long userId);

    RequestEntity cancelRequest(Long userId, Long requestId);

    List<RequestEntity> updateStatusRequests(Long userId, Long eventId, List<Long> requestIds, UpdateRequestStatus status);
}
