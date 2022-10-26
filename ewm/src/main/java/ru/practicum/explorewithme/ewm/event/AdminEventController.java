package ru.practicum.explorewithme.ewm.event;

import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.explorewithme.ewm.event.db.Event;
import ru.practicum.explorewithme.ewm.event.dto.AdminCommentDto;
import ru.practicum.explorewithme.ewm.event.dto.EventFullDto;
import ru.practicum.explorewithme.ewm.event.dto.EventState;
import ru.practicum.explorewithme.ewm.event.dto.NewEventDto;
import ru.practicum.explorewithme.ewm.event.service.EventMapper;
import ru.practicum.explorewithme.ewm.event.service.EventService;

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
    public List<EventFullDto> getAll(@RequestParam(name = "users", required = false) List<Long> users,
                                     @RequestParam(name = "states", required = false) List<EventState> states,
                                     @RequestParam(name = "categories", required = false) List<Long> categories,
                                     @RequestParam(name = "rangeStart", required = false) LocalDateTime rangeStart,
                                     @RequestParam(name = "rangeEnd", required = false) LocalDateTime rangeEnd,
                                     @RequestParam(name = "from", defaultValue = "0") Integer from,
                                     @RequestParam(name = "size", defaultValue = "10") Integer size) {
        return mapper.toDto(eventService.getAll(users, states, categories, rangeStart, rangeEnd, from, size));
    }

    @PutMapping("{eventId}")
    public EventFullDto update(@PathVariable @NotNull Long eventId, @RequestBody NewEventDto eventDto) {
        final Event event = mapper.toEntity(eventDto);
        event.setId(eventId);
        return mapper.toDto(eventService.update(event, true));
    }

    @PatchMapping("{eventId}/publish")
    public EventFullDto publish(@PathVariable @NotNull Long eventId) {
        return mapper.toDto(eventService.setEventState(eventId, EventState.PUBLISHED, null));
    }

    @PatchMapping("{eventId}/reject")
    public EventFullDto reject(@PathVariable @NotNull Long eventId) {
        return mapper.toDto(eventService.setEventState(eventId, EventState.CANCELED, null));
    }

    @PostMapping("{eventId}/reject")
    public EventFullDto rejectWithComment(@PathVariable @NotNull Long eventId, @Valid @RequestBody AdminCommentDto commentDto) {
        return mapper.toDto(eventService.setEventState(eventId, EventState.CANCELED, commentDto.getComment()));
    }
}
