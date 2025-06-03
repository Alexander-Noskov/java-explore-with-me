package ru.practicum.ewm.category;

import org.springframework.stereotype.Component;
import ru.practicum.ewm.category.dto.CategoryDto;
import ru.practicum.ewm.category.dto.NewCategoryDto;

@Component
public final class CategoryMapper {
    public CategoryEntity toCategoryEntity(NewCategoryDto dto) {
        if (dto == null) {
            return null;
        }
        return CategoryEntity.builder()
                .name(dto.getName())
                .build();
    }

    public CategoryDto toCategoryDto(CategoryEntity entity) {
        if (entity == null) {
            return null;
        }
        return CategoryDto.builder()
                .id(entity.getId())
                .name(entity.getName())
                .build();
    }
}
