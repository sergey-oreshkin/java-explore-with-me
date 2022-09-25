package ru.practicum.explorewithme.request.service;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.practicum.explorewithme.event.factory.EventFactory;
import ru.practicum.explorewithme.request.db.Request;
import ru.practicum.explorewithme.request.dto.RequestDto;
import ru.practicum.explorewithme.users.factory.UserFactory;

import java.util.List;

@Mapper(
        componentModel = "spring",
        uses = {UserFactory.class, EventFactory.class}
)
public interface RequestMapper {

    @Mapping(target = "requester", source = "requesterId")
    @Mapping(target = "event", source = "eventId")
    Request getEntity(Long requesterId, Long eventId);

    @Mapping(target = "requester", source = "requester.id")
    @Mapping(target = "event", source = "event.id")
    RequestDto toDto(Request request);

    List<RequestDto> toDto(List<Request> requests);
}
