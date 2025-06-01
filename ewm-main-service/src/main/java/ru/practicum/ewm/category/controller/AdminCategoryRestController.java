package ru.practicum.ewm.category.controller;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewm.category.CategoryMapper;
import ru.practicum.ewm.category.CategoryService;
import ru.practicum.ewm.category.dto.CategoryDto;
import ru.practicum.ewm.category.dto.NewCategoryDto;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("admin/categories")
@Validated
public class AdminCategoryRestController {
    private final CategoryService categoryService;
    private final CategoryMapper categoryMapper;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public CategoryDto addCategory(@RequestBody @Valid NewCategoryDto dto) {
        log.info("Adding category: {}", dto);
        return categoryMapper.toCategoryDto(categoryService.save(categoryMapper.toCategoryEntity(dto)));
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteCategory(@PathVariable @NotNull Long id) {
        log.info("Deleting category: {}", id);
        categoryService.deleteById(id);
    }

    @PatchMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public CategoryDto updateCategory(@PathVariable @NotNull Long id, @RequestBody @Valid NewCategoryDto dto) {
        log.info("Updating category id: {}, dto: {}", id, dto);
        return categoryMapper.toCategoryDto(categoryService.update(id, categoryMapper.toCategoryEntity(dto)));
    }
}
