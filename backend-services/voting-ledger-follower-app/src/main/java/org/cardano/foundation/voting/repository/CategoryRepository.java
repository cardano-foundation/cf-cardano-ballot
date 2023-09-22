package org.cardano.foundation.voting.repository;

import org.cardano.foundation.voting.domain.entity.Category;
import org.cardano.foundation.voting.domain.entity.CategoryId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface CategoryRepository extends JpaRepository<Category, CategoryId> {

    @Query("DELETE FROM Category c WHERE c.absoluteSlot > :slot")
    @Modifying
    void deleteAllAfterSlot(@Param("slot") long slot);

}
