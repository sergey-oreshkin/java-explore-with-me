package ru.practicum.explorewithme.event.db;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.practicum.explorewithme.event.dto.EventState;
import ru.practicum.explorewithme.users.db.User;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface EventRepository extends JpaRepository<Event, Long> {

    List<Event> findAllByInitiatorId(Long userId, Pageable pageable);

    Optional<Event> findByIdAndInitiator(Long id, User initiator);

    @Query("SELECT e FROM Event AS e WHERE" +
            " (e.initiator.id IN (:users) OR :users IS NULL)" +
            " AND (e.state IN (:states) OR :states IS NULL)" +
            " AND (e.category.id IN (:categories) OR :categories IS NULL)" +
            " AND (e.eventDate > :rangeStart OR :rangeStart IS NULL)" +
            " AND (e.eventDate < :rangeEnd OR :rangeEnd IS NULL)"
    )
    List<Event> findAllByParameters(@Param("users") List<Long> users, @Param("states") List<EventState> states,
                                    @Param("categories") List<Long> categories, @Param("rangeStart") LocalDateTime rangeStart,
                                    @Param("rangeEnd") LocalDateTime rangeEnd, Pageable pageable);
}
