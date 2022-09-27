package ru.practicum.explorewithme.request.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.explorewithme.event.dto.EventState;
import ru.practicum.explorewithme.event.service.EventService;
import ru.practicum.explorewithme.exception.ConflictException;
import ru.practicum.explorewithme.exception.NotFoundException;
import ru.practicum.explorewithme.exception.ValidationException;
import ru.practicum.explorewithme.request.db.Request;
import ru.practicum.explorewithme.request.db.RequestRepository;
import ru.practicum.explorewithme.request.dto.RequestState;

import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class RequestServiceImpl implements RequestService {

    private final RequestRepository requestRepository;

    private final EventService eventService;

    @Override
    public Request create(Request request) {
        requestRepository.findByRequesterIdAndEventId(request.getRequester().getId(), request.getEvent().getId())
                .ifPresent(r -> {
                    throw new ConflictException("Request already exist",
                            String.format("userId=%d, eventId=%d", r.getRequester().getId(), r.getEvent().getId()));
                });
        if (Objects.equals(request.getRequester().getId(), request.getEvent().getInitiator().getId())) {
            throw new ConflictException("Participation not available for initiator", String.format("InitiatorId=%d", request.getEvent().getInitiator().getId()));
        }
        if (request.getEvent().getState() != EventState.PUBLISHED) {
            throw new ConflictException("Event is not published yet", String.format("Event state is %s", request.getEvent().getState().toString()));
        }
        if (eventService.isRequestLimit(request.getEvent())) {
            throw new ConflictException("Request limit has been reached", String.format("Request limit=%d", request.getEvent().getParticipantLimit()));
        }
        if (request.getEvent().getParticipantLimit() == null || !request.getEvent().getRequestModeration() || request.getEvent().getParticipantLimit() == 0) {
            request.setState(RequestState.CONFIRMED);
        } else {
            request.setState(RequestState.PENDING);
        }
        return requestRepository.save(request);
    }

    @Override
    public List<Request> getAll(Long userId) {
        return requestRepository.findAllByRequesterId(userId);
    }

    @Override
    public Request cancel(Long userId, Long requestId) {
        Request request = requestRepository.findById(requestId)
                .orElseThrow(() -> new NotFoundException("Request not found", String.format("requestId=%d", requestId)));
        if (!Objects.equals(request.getRequester().getId(), userId)) {
            throw new ValidationException("Only requester can cancel request", String.format("userId=%d", userId));
        }
        requestRepository.delete(request);
        return request;
    }
}
