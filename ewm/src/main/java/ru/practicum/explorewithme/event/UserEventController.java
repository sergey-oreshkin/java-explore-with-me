package ru.practicum.explorewithme.event;

import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.explorewithme.event.db.Event;
import ru.practicum.explorewithme.event.dto.EventDto;
import ru.practicum.explorewithme.event.dto.NewEventDto;
import ru.practicum.explorewithme.event.service.EventMapper;
import ru.practicum.explorewithme.event.service.EventService;
import ru.practicum.explorewithme.request.dto.RequestDto;
import ru.practicum.explorewithme.request.service.RequestMapper;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.util.List;

@RestController
@RequestMapping("/users/{userId}/events")
@RequiredArgsConstructor
@Validated
public class UserEventController {

    private final EventService eventService;

    private final EventMapper eventMapper;

    private final RequestMapper requestMapper;

    @PostMapping
    public EventDto create(@Valid @RequestBody NewEventDto eventDto, @PathVariable @NotNull Long userId) {
        Event event = eventMapper.toEntity(eventDto, userId);
        return eventMapper.toDto(eventService.create(event));
    }

    @PatchMapping
    public EventDto update(@RequestBody NewEventDto eventDto, @PathVariable @NotNull Long userId) {
        Event event = eventMapper.toEntity(eventDto, userId);
        return eventMapper.toDto(eventService.update(event));
    }

    @GetMapping
    public List<EventDto> getAll(@PathVariable @NotNull Long userId,
                                 @RequestParam(name = "from", required = false) Integer from,
                                 @RequestParam(name = "size", required = false) Integer size) {
        return eventMapper.toDto(eventService.getAll(userId, from, size));
    }

    @GetMapping("{eventId}")
    public EventDto get(@PathVariable @NotNull Long userId, @PathVariable @NotNull Long eventId) {
        return eventMapper.toDto(eventService.get(userId, eventId));
    }

    @PatchMapping("{eventId}")
    public EventDto cancel(@PathVariable @NotNull Long userId, @PathVariable @NotNull Long eventId) {
        return eventMapper.toDto(eventService.cancel(userId, eventId));
    }

    @GetMapping("{eventId}/requests")
    public List<RequestDto> getRequests(@PathVariable @NotNull Long userId, @PathVariable @NotNull Long eventId) {
        return requestMapper.toDto(eventService.getRequests(userId, eventId));
    }

    @PatchMapping("{eventId}/requests/{reqId}/confirm")
    public RequestDto confirm(@PathVariable @NotNull Long userId,
                              @PathVariable @NotNull Long eventId,
                              @PathVariable @NotNull Long reqId) {
        return requestMapper.toDto(eventService.confirm(userId, eventId, reqId));
    }

    @PatchMapping("{eventId}/requests/{reqId}/reject")
    public RequestDto reject(@PathVariable @NotNull Long userId,
                             @PathVariable @NotNull Long eventId,
                             @PathVariable @NotNull Long reqId) {
        return requestMapper.toDto(eventService.reject(userId, eventId, reqId));
    }
}
