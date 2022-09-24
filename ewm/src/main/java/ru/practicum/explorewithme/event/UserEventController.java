package ru.practicum.explorewithme.event;

import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.explorewithme.event.db.Event;
import ru.practicum.explorewithme.event.dto.EventDto;
import ru.practicum.explorewithme.event.dto.NewEventDto;
import ru.practicum.explorewithme.event.service.EventMapper;
import ru.practicum.explorewithme.event.service.EventService;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.util.List;

@RestController
@RequestMapping("/users/{userId}/events")
@RequiredArgsConstructor
@Validated
public class UserEventController {

    private final EventService eventService;

    private final EventMapper mapper;

    @PostMapping
    public EventDto create(@Valid @RequestBody NewEventDto eventDto, @PathVariable @NotNull Long userId) {
        Event event = mapper.toEntity(eventDto, userId);
        return mapper.toDto(eventService.create(event));
    }

    @PatchMapping
    public EventDto update(@Valid @RequestBody NewEventDto eventDto, @PathVariable @NotNull Long userId) {
        Event event = mapper.toEntity(eventDto, userId);
        return mapper.toDto(eventService.update(event));
    }

    @GetMapping
    public List<EventDto> getAll(@PathVariable @NotNull Long userId,
                                 @RequestParam(name = "from", required = false) Integer from,
                                 @RequestParam(name = "size", required = false) Integer size) {
        return mapper.toDto(eventService.getAll(userId, from, size));
    }

    @GetMapping("{eventId}")
    public EventDto get(@PathVariable @NotNull Long userId, @PathVariable @NotNull Long eventId) {
        return mapper.toDto(eventService.get(userId, eventId));
    }

    @PatchMapping("{eventId}")
    public EventDto cancel(@PathVariable @NotNull Long userId, @PathVariable @NotNull Long eventId) {
        return mapper.toDto(eventService.cancel(userId, eventId));
    }
}
