package ru.practicum.explorewithme.ewm.event.db;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;
import ru.practicum.explorewithme.ewm.event.dto.EventState;
import ru.practicum.explorewithme.ewm.users.db.User;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class EventRepositoryFacade {

    private final EventJpaRepository jpaRepository;

    @Qualifier("eventCriteriaRepository")
    private final CustomEventRepository customRepository;

    public Optional<Event> findById(Long id) {
        return jpaRepository.findById(id);
    }

    public List<Event> findAllByInitiatorId(Long userId, Pageable pageable) {
        return jpaRepository.findAllByInitiatorId(userId, pageable);
    }

    public Optional<Event> findByIdAndInitiator(Long id, User initiator) {
        return jpaRepository.findByIdAndInitiator(id, initiator);
    }

    public Optional<Event> findByIdAndState(Long id, EventState state) {
        return jpaRepository.findByIdAndState(id, state);
    }

    public Event save(Event event) {
        return jpaRepository.save(event);
    }

    public List<Event> findAllByParameters(List<Long> users, List<EventState> states, List<Long> categories, Boolean paid,
                                           LocalDateTime rangeStart, LocalDateTime rangeEnd, String text, Boolean onlyAvailable,
                                           Integer from, Integer size) {
        return customRepository.findAllByParams(users, states, categories, paid, rangeStart, rangeEnd, text, from, size);
    }
}
