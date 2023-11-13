package org.cardano.foundation.voting.repository;

import org.cardano.foundation.voting.domain.entity.EventResultsCategoryResultsUtxoData;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UtxoCategoryResultsDataRepository extends JpaRepository<EventResultsCategoryResultsUtxoData, String> {

    List<EventResultsCategoryResultsUtxoData> findByAddress(String contractAddress);

    @Query("DELETE FROM EventResultsCategoryResultsUtxoData u WHERE u.absoluteSlot > :slot")
    @Modifying
    int deleteAllAfterSlot(@Param("slot") long slot);

}
