package ru.practicum.explorewithme.ewm.event.factory;

import ru.practicum.explorewithme.ewm.event.db.Event;

public interface EventFactory {
    Event getById(Long id);
}
