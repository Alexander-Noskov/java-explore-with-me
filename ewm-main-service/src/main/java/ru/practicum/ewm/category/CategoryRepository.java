package ru.practicum.ewm.category;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface CategoryRepository extends JpaRepository<CategoryEntity, Long> {
    @Query("select c from CategoryEntity c order by c.id limit :size offset :from")
    List<CategoryEntity> getCategoriesByParam(
            @Param("from") Integer from,
            @Param("size") Integer size
    );
}
