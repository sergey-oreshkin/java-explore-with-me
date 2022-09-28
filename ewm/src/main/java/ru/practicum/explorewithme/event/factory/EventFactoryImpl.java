package ru.practicum.explorewithme.event.factory;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.practicum.explorewithme.event.db.Event;
import ru.practicum.explorewithme.event.db.EventRepository;
import ru.practicum.explorewithme.exception.NotFoundException;
import ru.practicum.explorewithme.stats.StatsClient;
import ru.practicum.explorewithme.stats.dto.HitsDto;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class EventFactoryImpl implements EventFactory {

    public static final String BASE_URL = "/events";

    private final EventRepository eventRepository;

    private final StatsClient client;

    @Override
    public Event getById(Long id) {
        return eventRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Event not found", String.format("id=%d", id)));
    }

    @Override
    public long getViews(Long id) {
        Map<String, Object> parameters = new HashMap<>();
        parameters.put("uris", List.of(String.format("%s/%d", BASE_URL, id)));
        HitsDto hits = (HitsDto) client.get(parameters).getBody();
        if (hits == null) return 0;
        return hits.getHits();
    }
}
