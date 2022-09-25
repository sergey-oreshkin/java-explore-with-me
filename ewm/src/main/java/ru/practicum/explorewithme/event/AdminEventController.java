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

import javax.validation.Valid;
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
                                 @RequestParam(name = "rangeStart", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime rangeStart,
                                 @RequestParam(name = "rangeEnd", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime rangeEnd,
                                 @RequestParam(name = "from", required = false) Integer from,
                                 @RequestParam(name = "size", required = false) Integer size) {
        return mapper.toDto(eventService.getAll(users, states, categories, rangeStart, rangeEnd, from, size));
    }

    @PutMapping("{eventId}")
    public EventDto update(@PathVariable @NotNull Long eventId,
                           @Valid @RequestBody NewEventDto eventDto) {
        Event event = mapper.toEntity(eventDto);
        event.setId(eventId);
        return mapper.toDto(eventService.update(event));
    }

    @PatchMapping("{eventId}/{stateString}")
    public EventDto approve(@PathVariable @NotNull Long eventId,
                            @PathVariable @NotNull String stateString) {
        return mapper.toDto(eventService.approve(eventId, stateString));
    }
}
