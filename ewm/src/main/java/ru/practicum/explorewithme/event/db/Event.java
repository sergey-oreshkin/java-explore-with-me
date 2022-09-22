package ru.practicum.explorewithme.event.db;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import ru.practicum.explorewithme.category.db.Category;
import ru.practicum.explorewithme.event.dto.EventState;
import ru.practicum.explorewithme.users.db.User;

import javax.persistence.*;
import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "events")
public class Event {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;

    private String annotation;

    @ManyToOne
    @JoinColumn(name = "initiator")
    private User initiator;

    @ManyToOne
    @JoinColumn(name = "category")
    private Category category;

    private String description;

    private LocalDateTime eventDate;

    private Boolean paid;

    private Integer participantLimit;

    private Boolean requestModeration;

    private LocalDateTime published;

    @Enumerated(EnumType.STRING)
    private EventState state;

    @CreationTimestamp
    private LocalDateTime created;

    private Double latitude;

    private Double longitude;
}
