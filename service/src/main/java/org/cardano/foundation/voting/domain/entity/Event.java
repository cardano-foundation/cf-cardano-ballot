package org.cardano.foundation.voting.domain.entity;

import jakarta.persistence.*;
import lombok.*;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "event")
@Getter
@Setter
@Builder
@RequiredArgsConstructor
@AllArgsConstructor
@ToString
public class Event extends AbstractTimestampEntity {

    @Id
    @Column
    private String id; // e.g. 90ed2df9-dd21-4567-90e2-e8f09b9c422c

    @Column
    private String team; // e.g. CF Team // TODO what about team spoofing - do we need that team has private / public key

    @Column
    private String name; // e.g. Voltaire_Pre_Ratification

    @Column
    private String presentationName; // e.g. Voltaire Pre-Ratification

    @Column
    @Nullable
    private String description;

    @OneToMany(
            mappedBy = "event",
            cascade = CascadeType.ALL,
            fetch = FetchType.EAGER,
            orphanRemoval = true
    )
    @Builder.Default
    private List<Category> categories = new ArrayList<>();

}
