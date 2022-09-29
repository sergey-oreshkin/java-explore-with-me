package ru.practicum.explorewithme.event.factory;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.practicum.explorewithme.event.db.Event;
import ru.practicum.explorewithme.event.db.EventRepository;
import ru.practicum.explorewithme.exception.NotFoundException;
import ru.practicum.explorewithme.stats.StatsClient;
import ru.practicum.explorewithme.stats.dto.HitsDto;

import java.util.HashMap;
import java.util.Map;

@Component
@Slf4j
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
        Map<String, String> parameters = new HashMap<>();
        String uri = String.format("%s/%d", BASE_URL, id);
        parameters.put("uris", uri);
        try {
            HitsDto hits = client.get(parameters).stream().filter(h -> uri.equals(h.getUri())).findFirst().orElse(null);
            if (hits == null) return 0;
            return hits.getHits();
        } catch (Exception ex) {
            log.error(ex.getMessage());
        }
        return 0;
    }
}
