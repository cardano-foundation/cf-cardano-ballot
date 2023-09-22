package org.cardano.foundation.voting.domain.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.Immutable;

import javax.annotation.Nullable;
import java.util.Optional;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "proposal")
@Immutable
@IdClass(ProposalId.class)
public class Proposal extends AbstractTimestampEntity {

    @Getter
    @Setter
    @ManyToOne
    @JoinColumn(name = "category_id")
    @Id
    private String categoryId;

    @Getter
    @Column(name = "id")
    @Setter
    @Id
    private String proposalId;

    @Column(name = "name")
    @Nullable
    private String name;

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

    @Override
    public String toString() {
        return "Proposal{" +
                "id='" + proposalId + '\'' +
                ", name='" + name + '\'' +
                '}';
    }

}
