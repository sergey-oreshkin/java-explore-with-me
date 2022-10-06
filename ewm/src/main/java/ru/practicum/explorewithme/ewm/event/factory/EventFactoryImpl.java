package ru.practicum.explorewithme.ewm.event.factory;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.practicum.explorewithme.ewm.event.db.Event;
import ru.practicum.explorewithme.ewm.event.db.EventJpaRepository;
import ru.practicum.explorewithme.ewm.exception.NotFoundException;
import ru.practicum.explorewithme.ewm.stats.HttpClient;

@Component
@Slf4j
@RequiredArgsConstructor
public class EventFactoryImpl implements EventFactory {

    private final EventJpaRepository eventRepository;

    private final HttpClient client;

    @Override
    public Event getById(Long id) {
        return eventRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Event not found", String.format("id=%d", id)));
    }
}
