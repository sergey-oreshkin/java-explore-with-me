package ru.practicum.explorewithme.ewm.event.service;

import org.mapstruct.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import ru.practicum.explorewithme.ewm.category.factory.CategoryFactory;
import ru.practicum.explorewithme.ewm.common.SortType;
import ru.practicum.explorewithme.ewm.configuration.PropertiesService;
import ru.practicum.explorewithme.ewm.event.db.Event;
import ru.practicum.explorewithme.ewm.event.dto.EventFullDto;
import ru.practicum.explorewithme.ewm.event.dto.EventPublicDto;
import ru.practicum.explorewithme.ewm.event.dto.NewEventDto;
import ru.practicum.explorewithme.ewm.request.dto.RequestState;
import ru.practicum.explorewithme.ewm.stats.HttpClient;
import ru.practicum.explorewithme.ewm.stats.dto.HitsDto;
import ru.practicum.explorewithme.ewm.users.factory.UserFactory;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Mapper(
        componentModel = "spring",
        uses = {UserFactory.class, CategoryFactory.class}
)
public abstract class EventMapper {

    private final Logger log = LoggerFactory.getLogger(EventMapper.class);

    @Autowired
    private HttpClient client;

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
    @Mapping(target = "confirmedRequests", expression = "java(getConfirmedRequests(event))")
    public abstract EventPublicDto toPublicDto(Event event);

    @Mapping(target = "publishedOn", source = "event.published")
    @Mapping(target = "createdOn", source = "event.created")
    @Mapping(target = "location.lat", source = "event.latitude")
    @Mapping(target = "location.lon", source = "event.longitude")
    @Mapping(target = "views", expression = "java(getViews(event))")
    @Mapping(target = "confirmedRequests", expression = "java(getConfirmedRequests(event))")
    public abstract EventFullDto toDto(Event event);

    public abstract List<EventFullDto> toDto(List<Event> events);

    public abstract List<EventPublicDto> toPublicDto(List<Event> events);

    public List<EventFullDto> toDto(List<Event> events, SortType sortType) {
        if (Objects.isNull(sortType)) {
            return toDto(events);
        }
        Comparator<EventFullDto> comparator = Comparator.comparing(EventFullDto::getEventDate);
        if (sortType == SortType.VIEWS) {
            comparator = Comparator.comparing(EventFullDto::getViews);
        }
        List<EventFullDto> eventDtos = toDto(events);
        eventDtos.sort(comparator);
        return eventDtos;
    }

    public List<EventPublicDto> toPublicDto(List<Event> events, SortType sortType) {
        if (Objects.isNull(sortType)) {
            return toPublicDto(events);
        }
        Comparator<EventPublicDto> comparator = Comparator.comparing(EventPublicDto::getEventDate);
        if (sortType == SortType.VIEWS) {
            comparator = Comparator.comparing(EventPublicDto::getViews);
        }
        List<EventPublicDto> eventDtos = toPublicDto(events);
        eventDtos.sort(comparator);
        return eventDtos;
    }

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    public abstract void updateEvent(Event newEvent, @MappingTarget Event updatedEvent);

    protected long getConfirmedRequests(Event event) {
        if (event.getRequests() == null) {
            return 0L;
        }
        return event.getRequests().stream().filter(r -> r.getState() == RequestState.CONFIRMED).count();
    }

    protected long getViews(Event event) {
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
