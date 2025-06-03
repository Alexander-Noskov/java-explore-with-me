package ru.practicum.ewm.request;

import org.springframework.stereotype.Component;
import ru.practicum.ewm.request.dto.ParticipationRequestDto;
import ru.practicum.ewm.request.dto.StatusUpdateResultDto;

import java.util.List;

@Component
public final class RequestMapper {
    public ParticipationRequestDto toParticipationRequestDto(final RequestEntity requestEntity) {
        if (requestEntity == null) {
            return null;
        }
        return ParticipationRequestDto.builder()
                .id(requestEntity.getId())
                .status(requestEntity.getStatus())
                .created(requestEntity.getCreated().toLocalDateTime())
                .requester(requestEntity.getRequester().getId())
                .event(requestEntity.getEvent().getId())
                .build();
    }

    public StatusUpdateResultDto toStatusUpdateResultDto(final List<RequestEntity> requestEntities) {
        if (requestEntities == null) {
            return null;
        }
        return StatusUpdateResultDto.builder()
                .confirmedRequests(requestEntities.stream()
                        .filter(r -> r.getStatus().equals(RequestStatus.CONFIRMED))
                        .map(this::toParticipationRequestDto)
                        .toList())
                .rejectedRequests(requestEntities.stream()
                        .filter(r -> r.getStatus().equals(RequestStatus.REJECTED))
                        .map(this::toParticipationRequestDto)
                        .toList())
                .build();
    }
}
