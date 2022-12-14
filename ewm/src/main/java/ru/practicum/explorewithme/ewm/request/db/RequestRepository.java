package ru.practicum.explorewithme.ewm.request.db;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface RequestRepository extends JpaRepository<Request, Long> {

    Optional<Request> findByRequesterIdAndEventId(Long requesterId, Long eventId);

    List<Request> findAllByRequesterId(Long requesterId);
}
