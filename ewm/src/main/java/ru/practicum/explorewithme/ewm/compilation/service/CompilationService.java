package ru.practicum.explorewithme.ewm.compilation.service;

import ru.practicum.explorewithme.ewm.compilation.db.Compilation;

import java.util.List;

public interface CompilationService {

    Compilation create(Compilation compilation);

    void delete(Long compId);

    void pin(Long compId, boolean pinned);

    void addEvent(Long compId, Long eventId);

    void deleteEvent(Long compId, Long eventId);

    List<Compilation> getAll(Integer from, Integer size, Boolean pinned);

    Compilation getById(Long compId);
}
