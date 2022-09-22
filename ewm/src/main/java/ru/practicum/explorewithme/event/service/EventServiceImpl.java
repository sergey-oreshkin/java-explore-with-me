package ru.practicum.explorewithme.event.service;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import ru.practicum.explorewithme.common.OffsetLimitPageable;
import ru.practicum.explorewithme.event.db.Event;
import ru.practicum.explorewithme.event.db.EventRepository;
import ru.practicum.explorewithme.event.dto.EventDto;
import ru.practicum.explorewithme.event.dto.EventState;
import ru.practicum.explorewithme.exception.ConflictException;
import ru.practicum.explorewithme.exception.NotFoundException;
import ru.practicum.explorewithme.exception.ValidationException;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
@RequiredArgsConstructor
public class EventServiceImpl {

    private final EventRepository eventRepository;

    private final EventMapper mapper;

    @Value("${app.minTimeToEventMinutes}")
    private int minTimeToEventMinutes;

    public Event create(Event event) {
        validateEventDateOrThrow(event.getEventDate());
        event.setState(EventState.PENDING);
        return eventRepository.save(event);
    }

    public Event update(Event event) {
        if(event.getId() == null) {
            throw new ValidationException("Event id must not be null", String.valueOf(event.getId()));
        }
        Event oldEvent = eventRepository.findById(event.getId())
                .orElseThrow(() -> new NotFoundException("Event not found", String.format("id=%d", event.getId())));
        if (oldEvent.getState() == EventState.PUBLISHED) {
            throw new ConflictException("Published event can not be changed", EventState.PUBLISHED.toString());
        }
        if (event.getEventDate() != null) {
            validateEventDateOrThrow(event.getEventDate());
        }
        mapper.updateEvent(event, oldEvent);
        return eventRepository.save(oldEvent);
    }

    private void validateEventDateOrThrow(LocalDateTime eventDate) {
        LocalDateTime minTime = LocalDateTime.now().plusMinutes(minTimeToEventMinutes);
        if (minTime.isAfter(eventDate)) {
            throw new ValidationException(String.format("Must be minimum %d minutes before event start", minTimeToEventMinutes),
                    eventDate.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
        }
    }

    public List<Event> getAll(Long userId, Integer from, Integer size) {
        Pageable pageable = OffsetLimitPageable.of(from, size);
        return eventRepository.findAllByInitiatorId(userId, pageable);
    }
}
