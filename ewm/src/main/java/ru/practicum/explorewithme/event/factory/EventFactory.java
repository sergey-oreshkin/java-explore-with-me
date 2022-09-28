package ru.practicum.explorewithme.event.factory;

import ru.practicum.explorewithme.event.db.Event;

public interface EventFactory {
    Event getById(Long id);

    long getViews(Long id);
}
