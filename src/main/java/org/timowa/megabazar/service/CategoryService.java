package org.timowa.megabazar.service;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;
import org.timowa.megabazar.database.entity.Category;
import org.timowa.megabazar.database.repository.CategoryRepository;
import org.timowa.megabazar.dto.category.CategoryCreateDto;
import org.timowa.megabazar.dto.category.CategoryReadDto;
import org.timowa.megabazar.exception.CategoryAlreadyExistsException;
import org.timowa.megabazar.exception.CategoryNotFoundException;
import org.timowa.megabazar.mapper.category.CategoryCreateMapper;
import org.timowa.megabazar.mapper.category.CategoryReadMapper;

import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@Validated
@Transactional
@RequiredArgsConstructor
public class CategoryService {
    private final CategoryRepository categoryRepository;
    private final CategoryCreateMapper categoryCreateMapper;
    private final CategoryReadMapper categoryReadMapper;

    public CategoryReadDto createCategory(@Valid CategoryCreateDto createDto) {
        if (categoryRepository.findByName(createDto.getName()).isPresent()) {
            throw new CategoryAlreadyExistsException("Category with name: " + createDto.getName() + " already exists");
        }
        Category savedCategory = categoryRepository.save(categoryCreateMapper.map(createDto));
        return categoryReadMapper.map(savedCategory);
    }

    public CategoryReadDto findById(Long id) {
        Optional<Category> checkCategory = categoryRepository.findById(id);
        if (checkCategory.isEmpty()) {
            throw new CategoryNotFoundException("Category with id: " + id + " not found");
        }
        return categoryReadMapper.map(checkCategory.get());
    }

    public List<Category> findAll() {
        return categoryRepository.findAll();
    }

    public Page<Category> findAll(Pageable pageable) {
        return categoryRepository.findAll(pageable);
    }

    public void delete(Long id) {
        Optional<Category> category = categoryRepository.findById(id);
        if (category.isEmpty()) {
            throw new CategoryNotFoundException("Category with id: " + id + " not found");
        }
        if (category.get().getProducts() != null)
            category.get().getProducts().forEach(product -> product.setCategory(null));
        categoryRepository.deleteById(id);
        String successDelete = "Category with id: " + id + " successfully deleted";
        log.info(successDelete);
    }
}
