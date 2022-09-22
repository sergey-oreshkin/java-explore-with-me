package ru.practicum.explorewithme.event.service;

import org.mapstruct.*;
import ru.practicum.explorewithme.category.factory.CategoryFactory;
import ru.practicum.explorewithme.event.db.Event;
import ru.practicum.explorewithme.event.dto.EventDto;
import ru.practicum.explorewithme.event.dto.NewEventDto;
import ru.practicum.explorewithme.users.factory.UserFactory;

import java.util.List;

@Mapper(
        componentModel = "spring",
        uses = {UserFactory.class, CategoryFactory.class}
)
public interface EventMapper {
    @Mapping(target = "initiator", source = "userId")
    @Mapping(target = "id", source = "eventDto.eventId")
    @Mapping(target = "latitude", source = "eventDto.location.lat")
    @Mapping(target = "longitude", source = "eventDto.location.lon")
    Event toEntity(NewEventDto eventDto, Long userId);

    @Mapping(target = "publishedOn", source = "event.published")
    @Mapping(target = "createdOn", source = "event.created")
    @Mapping(target = "location.lat", source = "event.latitude")
    @Mapping(target = "location.lon", source = "event.longitude")
    EventDto toDto(Event event);

    List<EventDto> toDto(List<Event> events);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateEvent(Event newEvent, @MappingTarget Event updatedEvent);
}
