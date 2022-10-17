package ru.practicum.explorewithme.ewm.event.db;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.explorewithme.ewm.event.dto.EventState;
import ru.practicum.explorewithme.ewm.users.db.User;

import java.util.List;
import java.util.Optional;

public interface EventJpaRepository extends JpaRepository<Event, Long> {

    @EntityGraph(attributePaths = {"initiator", "category"})
    List<Event> findAllByInitiatorId(Long userId, Pageable pageable);

    @EntityGraph(attributePaths = {"initiator", "category"})
    Optional<Event> findByIdAndInitiator(Long id, User initiator);

    @EntityGraph(attributePaths = {"initiator", "category"})
    Optional<Event> findByIdAndState(Long id, EventState state);
}
