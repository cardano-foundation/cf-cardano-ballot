package org.cardano.foundation.voting.repository;

import org.cardano.foundation.voting.domain.entity.Event;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface EventRepository extends JpaRepository<Event, String> {

    @Query("DELETE FROM Event e WHERE e.absoluteSlot > :slot")
    @Modifying
    int deleteAllAfterSlot(@Param("slot") long slot);

}
