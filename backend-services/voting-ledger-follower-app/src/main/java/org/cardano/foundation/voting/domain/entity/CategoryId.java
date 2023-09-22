package org.cardano.foundation.voting.domain.entity;

import lombok.*;

import java.io.Serializable;

@AllArgsConstructor
@Getter
@Setter
@EqualsAndHashCode
@NoArgsConstructor
public class CategoryId implements Serializable {

    private String eventId;

    private String categoryId;

}
