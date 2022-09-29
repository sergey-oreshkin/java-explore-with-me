package ru.practicum.explorewithme.event;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.explorewithme.common.SortType;
import ru.practicum.explorewithme.event.dto.EventDto;
import ru.practicum.explorewithme.event.service.EventMapper;
import ru.practicum.explorewithme.event.service.EventService;
import ru.practicum.explorewithme.stats.StatsClient;

import javax.servlet.http.HttpServletRequest;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/events")
@RequiredArgsConstructor
@Validated
public class PublicEventController {

    private final EventService eventService;

    private final EventMapper eventMapper;

    private final StatsClient client;

    @GetMapping("{eventId}")
    public EventDto get(@PathVariable @NotNull Long eventId, HttpServletRequest request) {
        try {
            client.hit(request);
        } catch (Exception ex) {
            log.error(ex.getMessage());
        }
        return eventMapper.toDto(eventService.getPublishedById(eventId));
    }

    @GetMapping
    public List<EventDto> getAll(@RequestParam(name = "text", required = false) String text,
                                 @RequestParam(name = "categories", required = false) List<Long> categories,
                                 @RequestParam(name = "paid", required = false) Boolean paid,
                                 @RequestParam(name = "onlyAvailable", required = false) Boolean onlyAvailable,
                                 @RequestParam(name = "sort", required = false) SortType sortType,
                                 @RequestParam(name = "rangeStart", required = false) @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime rangeStart,
                                 @RequestParam(name = "rangeEnd", required = false) @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime rangeEnd,
                                 @RequestParam(name = "from", defaultValue = "0") Integer from,
                                 @RequestParam(name = "size", defaultValue = "10") Integer size,
                                 HttpServletRequest request) {
        try {
            client.hit(request);
        } catch (Exception ex) {
            log.error(ex.getMessage());
        }
        return eventMapper.toDto(eventService.getAllPublished(text, categories, paid, onlyAvailable, sortType, rangeStart, rangeEnd, from, size));
    }
}
