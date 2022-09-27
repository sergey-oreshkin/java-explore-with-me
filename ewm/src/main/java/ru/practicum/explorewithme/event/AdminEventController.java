package ru.practicum.explorewithme.event;

import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.explorewithme.event.db.Event;
import ru.practicum.explorewithme.event.dto.EventDto;
import ru.practicum.explorewithme.event.dto.EventState;
import ru.practicum.explorewithme.event.dto.NewEventDto;
import ru.practicum.explorewithme.event.service.EventMapper;
import ru.practicum.explorewithme.event.service.EventService;

import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/admin/events")
@RequiredArgsConstructor
@Validated
public class AdminEventController {

    private final EventMapper mapper;

    private final EventService eventService;

    @GetMapping
    public List<EventDto> getAll(@RequestParam(name = "users", required = false) List<Long> users,
                                 @RequestParam(name = "states", required = false) List<EventState> states,
                                 @RequestParam(name = "categories", required = false) List<Long> categories,
                                 @RequestParam(name = "rangeStart", required = false) @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime rangeStart,
                                 @RequestParam(name = "rangeEnd", required = false) @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime rangeEnd,
                                 @RequestParam(name = "from", defaultValue = "0") Integer from,
                                 @RequestParam(name = "size", defaultValue = "10") Integer size) {
        return mapper.toDto(eventService.getAll(users, states, categories, rangeStart, rangeEnd, from, size));
    }

    @PutMapping("{eventId}")
    public EventDto update(@PathVariable @NotNull Long eventId, @RequestBody NewEventDto eventDto) {
        Event event = mapper.toEntity(eventDto);
        event.setId(eventId);
        return mapper.toDto(eventService.update(event));
    }

    @PatchMapping("{eventId}/publish")
    public EventDto publish(@PathVariable @NotNull Long eventId) {
        return mapper.toDto(eventService.setEventState(eventId, EventState.PUBLISHED));
    }

    @PatchMapping("{eventId}/reject")
    public EventDto reject(@PathVariable @NotNull Long eventId) {
        return mapper.toDto(eventService.setEventState(eventId, EventState.CANCELED));
    }
}
