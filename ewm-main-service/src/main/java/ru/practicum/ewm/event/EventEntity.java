package ru.practicum.ewm.event;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.ewm.category.CategoryEntity;
import ru.practicum.ewm.compilation.CompilationEntity;
import ru.practicum.ewm.request.RequestEntity;
import ru.practicum.ewm.user.UserEntity;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "events", schema = "public")
public class EventEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @Column(name = "title", nullable = false, length = 128)
    private String title;

    @Column(name = "annotation", nullable = false, length = 2048)
    private String annotation;

    @Column(name = "description", nullable = false, length = 8192)
    private String description;

    @Column(name = "event_date", nullable = false)
    private LocalDateTime eventDate;

    @Column(name = "location_lat", nullable = false)
    private BigDecimal lat;

    @Column(name = "location_lon", nullable = false)
    private BigDecimal lon;

    @Column(name = "participant_limit", nullable = false)
    private Integer participantLimit;

    @Column(name = "paid", nullable = false)
    private Boolean paid;

    @Column(name = "request_moderation", nullable = false)
    private Boolean requestModeration;

    @Column(name = "created_on", nullable = false)
    private LocalDateTime createdOn;

    @Column(name = "published_on", nullable = true)
    private LocalDateTime publishedOn;

    @Column(name = "state", nullable = false)
    @Enumerated(EnumType.STRING)
    private EventState state;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "initiator_id", nullable = false)
    private UserEntity initiator;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "category_id", nullable = false)
    private CategoryEntity category;

    @OneToMany(mappedBy = "event", fetch = FetchType.EAGER)
    private List<RequestEntity> requests;

    @ManyToMany(mappedBy = "events")
    private List<CompilationEntity> compilations = new ArrayList<>();
}
