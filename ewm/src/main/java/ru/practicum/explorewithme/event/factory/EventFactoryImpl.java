package ru.practicum.explorewithme.event.factory;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.practicum.explorewithme.event.db.Event;
import ru.practicum.explorewithme.event.db.EventRepository;
import ru.practicum.explorewithme.exception.NotFoundException;

@Component
@RequiredArgsConstructor
public class EventFactoryImpl implements EventFactory {

    private final EventRepository eventRepository;

    @Override
    public Event getById(Long id) {
        return eventRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Event not found", String.format("id=%d", id)));
    }
}
