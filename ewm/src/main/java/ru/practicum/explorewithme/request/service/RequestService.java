package ru.practicum.explorewithme.request.service;

import ru.practicum.explorewithme.request.db.Request;

import java.util.List;

public interface RequestService {
    Request create(Request request);

    List<Request> getAll(Long userId);

    Request cancel(Long userId, Long requestId);
}
