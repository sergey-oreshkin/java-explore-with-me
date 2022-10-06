package ru.practicum.explorewithme.ewm.event.db;

import ru.practicum.explorewithme.ewm.event.dto.EventState;

import java.time.LocalDateTime;
import java.util.List;

public interface CustomEventRepository {
    List<Event> findAllByParams(List<Long> users, List<EventState> states, List<Long> categories, Boolean paid,
                                LocalDateTime rangeStart, LocalDateTime rangeEnd, String text,
                                Integer from, Integer size);
}
