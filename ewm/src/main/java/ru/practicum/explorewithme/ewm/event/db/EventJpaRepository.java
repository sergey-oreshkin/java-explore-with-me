package ru.practicum.explorewithme.ewm.event.db;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.explorewithme.ewm.event.dto.EventState;
import ru.practicum.explorewithme.ewm.users.db.User;

import java.util.List;
import java.util.Optional;

public interface EventJpaRepository extends JpaRepository<Event, Long> {

    List<Event> findAllByInitiatorId(Long userId, Pageable pageable);

    Optional<Event> findByIdAndInitiator(Long id, User initiator);

    Optional<Event> findByIdAndState(Long id, EventState state);
}
