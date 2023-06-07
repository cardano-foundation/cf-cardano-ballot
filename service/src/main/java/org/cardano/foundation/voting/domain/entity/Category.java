package org.cardano.foundation.voting.domain.entity;

import jakarta.persistence.*;
import lombok.*;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "category")
@ToString
public class Category extends AbstractTimestampEntity {

    @Id
    @Column(name = "id")
    private String id;

    @Column(name = "name")
    private String name;

    @Column(name = "presentation_name")
    private String presentationName;

    @Column(name = "description")
    @Nullable
    private String description;

    @ManyToOne(
            fetch = FetchType.EAGER, cascade = CascadeType.ALL
    )
    @JoinColumn(name = "event_id")
    private Event event;

    @OneToMany(
            mappedBy = "category",
            cascade = CascadeType.ALL,
            fetch = FetchType.EAGER,
            orphanRemoval = true
    )
    @Builder.Default
    private List<Proposal> proposals =  new ArrayList<>();

}
