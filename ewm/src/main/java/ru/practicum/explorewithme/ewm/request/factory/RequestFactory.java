package ru.practicum.explorewithme.ewm.request.factory;

import ru.practicum.explorewithme.ewm.request.db.Request;

public interface RequestFactory {
    Request getById(Long id);
}
