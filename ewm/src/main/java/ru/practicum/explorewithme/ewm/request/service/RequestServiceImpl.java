package ru.practicum.explorewithme.ewm.request.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.explorewithme.ewm.event.dto.EventState;
import ru.practicum.explorewithme.ewm.event.service.EventService;
import ru.practicum.explorewithme.ewm.exception.ConflictException;
import ru.practicum.explorewithme.ewm.exception.NotFoundException;
import ru.practicum.explorewithme.ewm.exception.ValidationException;
import ru.practicum.explorewithme.ewm.request.db.Request;
import ru.practicum.explorewithme.ewm.request.db.RequestRepository;
import ru.practicum.explorewithme.ewm.request.dto.RequestState;

import java.util.List;
import java.util.Objects;

import static java.lang.String.format;

@Service
@RequiredArgsConstructor
public class RequestServiceImpl implements RequestService {

    private final RequestRepository requestRepository;

    private final EventService eventService;

    @Override
    @Transactional
    public Request create(Request request) {
        requestRepository.findByRequesterIdAndEventId(request.getRequester().getId(), request.getEvent().getId())
                .ifPresent(r -> {
                    throw new ConflictException("Request already exist",
                            format("UserId=%d, EventId=%d", r.getRequester().getId(), r.getEvent().getId()));
                });
        if (Objects.equals(request.getRequester().getId(), request.getEvent().getInitiator().getId())) {
            throw new ConflictException("Participation not available for initiator", format("InitiatorId=%d", request.getEvent().getInitiator().getId()));
        }
        if (request.getEvent().getState() != EventState.PUBLISHED) {
            throw new ConflictException("Event is not published yet", format("Event state is %s", request.getEvent().getState().toString()));
        }
        if (eventService.isRequestLimit(request.getEvent())) {
            throw new ConflictException("Request limit has been reached", format("Request limit=%d", request.getEvent().getParticipantLimit()));
        }
        if (!request.getEvent().getRequestModeration()) {
            request.setState(RequestState.CONFIRMED);
        } else {
            request.setState(RequestState.PENDING);
        }
        return requestRepository.save(request);
    }

    @Override
    public List<Request> getAllByRequester(Long userId) {
        return requestRepository.findAllByRequesterId(userId);
    }

    @Override
    public Request cancel(Long userId, Long requestId) {
        final Request request = requestRepository.findById(requestId)
                .orElseThrow(() -> new NotFoundException("Request not found", format("RequestId=%d", requestId)));
        if (!Objects.equals(request.getRequester().getId(), userId)) {
            throw new ValidationException("Only requester can cancel request", format("UserId=%d", userId));
        }
        requestRepository.delete(request);
        request.setState(RequestState.CANCELED);
        return request;
    }
}
