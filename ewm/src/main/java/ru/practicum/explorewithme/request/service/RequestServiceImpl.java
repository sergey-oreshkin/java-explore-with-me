package ru.practicum.explorewithme.request.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.explorewithme.event.dto.EventState;
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
public class RequestServiceImpl {

    private final RequestRepository requestRepository;

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
        long confirmedRequestsCount = request.getEvent().getRequests().stream().filter(r -> r.getState() == RequestState.CONFIRMED).count();
        if (confirmedRequestsCount >= request.getEvent().getParticipantLimit()) {
            throw new ConflictException("Request limit has been reached", String.format("Request limit=%d", request.getEvent().getParticipantLimit()));
        }
        if (request.getEvent().getRequestModeration()) {
            request.setState(RequestState.PENDING);
        } else {
            request.setState(RequestState.CONFIRMED);
        }
        return requestRepository.save(request);
    }

    public List<Request> getAll(Long userId) {
        return requestRepository.findAllByRequesterId(userId);
    }

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
