package ru.practicum.explorewithme.event.factory;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.practicum.explorewithme.event.db.Event;
import ru.practicum.explorewithme.event.db.EventRepository;
import ru.practicum.explorewithme.exception.NotFoundException;
import ru.practicum.explorewithme.stats.StatsClient;

@Component
@Slf4j
@RequiredArgsConstructor
public class EventFactoryImpl implements EventFactory {

    private final EventRepository eventRepository;

    private final StatsClient client;

    @Override
    public Event getById(Long id) {
        return eventRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Event not found", String.format("id=%d", id)));
    }
}
