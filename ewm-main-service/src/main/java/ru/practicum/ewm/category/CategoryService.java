package ru.practicum.ewm.category;

import java.util.List;

public interface CategoryService {
    CategoryEntity save(CategoryEntity category);

    void deleteById(Long id);

    CategoryEntity getById(Long id);

    CategoryEntity update(Long id, String name);

    List<CategoryEntity> getCategories(Integer from, Integer size);
}
