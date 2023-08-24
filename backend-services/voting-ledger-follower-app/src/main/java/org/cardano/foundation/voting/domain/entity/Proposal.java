package org.cardano.foundation.voting.domain.entity;

import jakarta.persistence.*;
import lombok.*;

import javax.annotation.Nullable;
import java.util.Optional;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "proposal")
public class Proposal extends AbstractTimestampEntity {

    @Getter
    @Id
    @Column
    @Setter
    private String id;

    @Column(name = "name")
    @Nullable
    private String name;

    @Getter
    @Setter
    @ManyToOne(
            fetch = FetchType.EAGER,
            cascade = CascadeType.ALL
    )
    @JoinColumn(name = "category_id")
    private Category category;

    @Getter
    @Setter
    @Column(name = "absolute_slot")
    private long absoluteSlot;

    public Optional<String> getName() {
        return Optional.ofNullable(name);
    }

    public void setName(@Nullable String name) {
        this.name = name;
    }

}
