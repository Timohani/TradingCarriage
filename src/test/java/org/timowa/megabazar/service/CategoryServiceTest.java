package org.timowa.megabazar.service;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.transaction.annotation.Transactional;
import org.timowa.megabazar.database.entity.Category;
import org.timowa.megabazar.dto.category.CategoryCreateDto;
import org.timowa.megabazar.dto.category.CategoryReadDto;
import org.timowa.megabazar.exception.CategoryAlreadyExistsException;
import org.timowa.megabazar.exception.CategoryNotFoundException;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
class CategoryServiceTest {

    @Autowired
    private CategoryService categoryService;

    @Test
    void createCategory() {
        CategoryCreateDto createDto = new CategoryCreateDto("Мебель", "Описание");
        CategoryReadDto readDto = categoryService.createCategory(createDto);
        CategoryReadDto checkCategory = categoryService.findById(readDto.getId());
        assertNotNull(checkCategory);
        assertEquals(readDto.getName(), checkCategory.getName());

        CategoryCreateDto sameCreateDto = new CategoryCreateDto("Мебель", "");
        assertThrows(CategoryAlreadyExistsException.class, () -> categoryService.createCategory(sameCreateDto));
    }

    @Test
    void findById() {
        CategoryCreateDto createDto = new CategoryCreateDto("Одежда", "");
        CategoryReadDto readDto = categoryService.createCategory(createDto);
        CategoryReadDto actual = categoryService.findById(readDto.getId());
        assertEquals(readDto.getName(), actual.getName());

        assertThrows(CategoryNotFoundException.class, () -> categoryService.findById(993L));
    }

    @Test
    void findAllByPageable() {
        Pageable sortedByProductsCount =
                PageRequest.of(0, 10, Sort.by("name"));
        Page<Category> page = categoryService.findAll(sortedByProductsCount);
        assertEquals(1, page.getTotalElements());
    }

    @Test
    void delete() {
        CategoryCreateDto createDto = new CategoryCreateDto("Принтеры", "");
        CategoryReadDto readDto = categoryService.createCategory(createDto);
        categoryService.delete(readDto.getId());
        assertThrows(CategoryNotFoundException.class, () -> categoryService.findById(readDto.getId()));
    }
}