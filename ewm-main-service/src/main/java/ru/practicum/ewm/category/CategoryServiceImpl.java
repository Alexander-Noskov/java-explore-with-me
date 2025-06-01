package ru.practicum.ewm.category;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.ewm.exception.NotFoundException;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CategoryServiceImpl implements CategoryService {
    private final CategoryRepository categoryRepository;

    @Override
    public CategoryEntity save(CategoryEntity category) {
        return categoryRepository.save(category);
    }

    @Override
    public void deleteById(Long id) {
        getById(id);
        categoryRepository.deleteById(id);
    }

    @Override
    public CategoryEntity getById(Long id) {
        return categoryRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Category with id=" + id + " was not found"));
    }

    @Override
    public CategoryEntity update(Long id, CategoryEntity category) {
        CategoryEntity categoryEntity = getById(id);
        categoryEntity.setName(category.getName());
        return categoryRepository.save(categoryEntity);
    }

    @Override
    public List<CategoryEntity> getCategories(Integer from, Integer size) {
        return categoryRepository.getCategoriesByParam(from, size);
    }
}
