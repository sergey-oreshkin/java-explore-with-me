package ru.practicum.explorewithme.request.factory;

import ru.practicum.explorewithme.request.db.Request;

public interface RequestFactory {
    Request getById(Long id);
}
