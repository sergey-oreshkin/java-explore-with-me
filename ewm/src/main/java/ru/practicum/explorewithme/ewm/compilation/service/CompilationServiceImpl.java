package ru.practicum.explorewithme.ewm.compilation.service;

import lombok.RequiredArgsConstructor;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.explorewithme.ewm.common.OffsetLimitPageable;
import ru.practicum.explorewithme.ewm.compilation.db.Compilation;
import ru.practicum.explorewithme.ewm.compilation.db.CompilationRepository;
import ru.practicum.explorewithme.ewm.event.db.Event;
import ru.practicum.explorewithme.ewm.event.factory.EventFactory;
import ru.practicum.explorewithme.ewm.exception.NotFoundException;
import ru.practicum.explorewithme.ewm.exception.ValidationException;

import java.util.List;
import java.util.Objects;

import static java.lang.String.format;

@Service
@RequiredArgsConstructor
public class CompilationServiceImpl implements CompilationService {

    private final CompilationRepository compilationRepository;

    private final EventFactory eventFactory;

    @Override
    public Compilation create(Compilation compilation) {
        return compilationRepository.save(compilation);
    }

    @Override
    public void delete(Long compId) {
        try {
            compilationRepository.deleteById(compId);
        } catch (EmptyResultDataAccessException ex) {
            throw new NotFoundException("Compilation not found", format("Id=%d", compId));
        }
    }

    @Override
    @Transactional
    public void pin(Long compId, boolean pinned) {
        final Compilation compilation = getCompilationOrThrow(compId);
        compilation.setPinned(pinned);
        compilationRepository.save(compilation);
    }

    @Override
    @Transactional
    public void addEvent(Long compId, Long eventId) {
        final Compilation compilation = getCompilationOrThrow(compId);
        final Event event = eventFactory.getById(eventId);
        if (compilation.getEvents().contains(event)) {
            throw new ValidationException("Event already in te compilation", format("EventId=%d", eventId));
        }
        compilation.getEvents().add(event);
        compilationRepository.save(compilation);
    }

    @Override
    @Transactional
    public void deleteEvent(Long compId, Long eventId) {
        final Compilation compilation = getCompilationOrThrow(compId);
        final Event event = eventFactory.getById(eventId);
        if (!compilation.getEvents().contains(event)) {
            throw new ValidationException("The event is not in the compilation", format("EventId=%d", eventId));
        }
        compilation.getEvents().remove(event);
        compilationRepository.save(compilation);
    }

    @Override
    public List<Compilation> getAll(Integer from, Integer size, Boolean pinned) {
        final Pageable pageable = OffsetLimitPageable.of(from, size);
        if (Objects.nonNull(pinned)) {
            return compilationRepository.findAllByPinned(pinned, pageable);
        }
        return compilationRepository.findAll(pageable).getContent();
    }

    @Override
    public Compilation getById(Long compId) {
        return getCompilationOrThrow(compId);
    }

    private Compilation getCompilationOrThrow(Long id) {
        return compilationRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Compilation not found", format("id=%d", id)));
    }
}
