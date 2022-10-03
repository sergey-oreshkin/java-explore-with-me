package ru.practicum.explorewithme.ewm.event.db;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import ru.practicum.explorewithme.ewm.event.dto.EventState;

import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Repository
@RequiredArgsConstructor
public class EventCriteriaRepository implements CustomEventRepository {

    private final EntityManager em;

    @Override
    public List<Event> findAllByParams(List<Long> users, List<EventState> states, List<Long> categories, Boolean paid,
                                       LocalDateTime rangeStart, LocalDateTime rangeEnd, String text, Boolean onlyAvailable,
                                       Integer from, Integer size) {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Event> query = cb.createQuery(Event.class);
        Root<Event> event = query.from(Event.class);
        List<Predicate> predicates = new ArrayList<>();

        if (Objects.nonNull(users) && !users.isEmpty()) {
            predicates.add(cb.in(event.get("initiator").get("id")).value(users));
        }
        if (Objects.nonNull(states) && !states.isEmpty()) {
            predicates.add(cb.in(event.get("state")).value(states));
        }
        if (Objects.nonNull(categories) && !categories.isEmpty()) {
            predicates.add(cb.in(event.get("category").get("id")).value(categories));
        }
        if (Objects.nonNull(paid)) {
            predicates.add(cb.equal(event.get("paid"), paid));
        }
        if (Objects.nonNull(rangeStart)) {
            predicates.add(cb.greaterThan(event.get("eventDate"), rangeStart));
        }
        if (Objects.nonNull(rangeEnd)) {
            predicates.add(cb.lessThan(event.get("eventDate"), rangeEnd));
        }
        if (Objects.nonNull(text)) {
            predicates.add(
                    cb.or(
                            cb.like(cb.upper(event.get("annotation")), "%" + text.toUpperCase() + "%"),
                            cb.like(cb.upper(event.get("description")), "%" + text.toUpperCase() + "%"))
            );
        }
        return em.createQuery(query.select(event).where(cb.and(predicates.toArray(predicates.toArray(new Predicate[]{})))))
                .setFirstResult(from)
                .setMaxResults(size)
                .getResultList();
    }
}
