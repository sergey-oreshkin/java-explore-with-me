package ru.practicum.explorewithme.event.service;

import org.mapstruct.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import ru.practicum.explorewithme.category.factory.CategoryFactory;
import ru.practicum.explorewithme.configuration.PropertiesService;
import ru.practicum.explorewithme.event.db.Event;
import ru.practicum.explorewithme.event.dto.EventDto;
import ru.practicum.explorewithme.event.dto.NewEventDto;
import ru.practicum.explorewithme.stats.StatsClient;
import ru.practicum.explorewithme.stats.dto.HitsDto;
import ru.practicum.explorewithme.users.factory.UserFactory;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Mapper(
        componentModel = "spring",
        uses = {UserFactory.class, CategoryFactory.class}
)
public abstract class EventMapper {

    private final Logger log = LoggerFactory.getLogger(EventMapper.class);

    @Autowired
    private StatsClient client;

    @Autowired
    private PropertiesService propertiesService;


    @Mapping(target = "initiator", source = "userId")
    @Mapping(target = "id", source = "eventDto.eventId")
    @Mapping(target = "latitude", source = "eventDto.location.lat")
    @Mapping(target = "longitude", source = "eventDto.location.lon")
    public abstract Event toEntity(NewEventDto eventDto, Long userId);

    @Mapping(target = "id", source = "eventDto.eventId")
    @Mapping(target = "latitude", source = "eventDto.location.lat")
    @Mapping(target = "longitude", source = "eventDto.location.lon")
    public abstract Event toEntity(NewEventDto eventDto);

    @Mapping(target = "publishedOn", source = "event.published")
    @Mapping(target = "createdOn", source = "event.created")
    @Mapping(target = "location.lat", source = "event.latitude")
    @Mapping(target = "location.lon", source = "event.longitude")
    @Mapping(target = "views", expression = "java(getViews(event))")
    public abstract EventDto toDto(Event event);

    public abstract List<EventDto> toDto(List<Event> events);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    public abstract void updateEvent(Event newEvent, @MappingTarget Event updatedEvent);

    public long getViews(Event event) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(propertiesService.getDateTimeFormat());
        Map<String, String> parameters = new HashMap<>();
        String uri = String.format("%s/%d", propertiesService.getBaseEventUrl(), event.getId());
        parameters.put("uris", uri);
        parameters.put("start", event.getCreated().format(formatter));
        parameters.put("end", LocalDateTime.now().format(formatter));
        try {
            HitsDto hits = client.get(parameters).stream()
                    .filter(h -> uri.equals(h.getUri())).findFirst().orElse(null);
            if (hits == null) {
                return 0;
            } else {
                return hits.getHits();
            }
        } catch (Exception ex) {
            log.error(ex.getMessage());
        }
        return 0;
    }
}
