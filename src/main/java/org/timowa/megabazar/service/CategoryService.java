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
import org.timowa.megabazar.database.entity.Product;
import org.timowa.megabazar.database.repository.CategoryRepository;
import org.timowa.megabazar.dto.category.CategoryCreateDto;
import org.timowa.megabazar.dto.category.CategoryReadDto;
import org.timowa.megabazar.exception.CategoryAlreadyExistsException;
import org.timowa.megabazar.exception.CategoryNotFoundException;
import org.timowa.megabazar.mapper.category.CategoryCreateMapper;
import org.timowa.megabazar.mapper.category.CategoryReadMapper;

@Slf4j
@Service
@Validated
@Transactional
@RequiredArgsConstructor
public class CategoryService {
    private final CategoryRepository categoryRepository;
    private final CategoryCreateMapper categoryCreateMapper;
    private final CategoryReadMapper categoryReadMapper;
    private final ProductService productService;

    public CategoryReadDto createCategory(@Valid CategoryCreateDto createDto) {
        if (categoryRepository.findByName(createDto.getName()).isPresent()) {
            throw new CategoryAlreadyExistsException("Category with name: " + createDto.getName() + " already exists");
        }
        Category savedCategory = categoryRepository.save(categoryCreateMapper.map(createDto));
        return categoryReadMapper.map(savedCategory);
    }

    public CategoryReadDto addProductToCategory(Long productId, Category category) {
        Product product = productService.getObjectById(productId);
        if (product.getCategory() == null) {
            product.setCategory(category);
            category.getProducts().add(product);
        } else {
            throw new IllegalArgumentException("Product already has category");
        }
        return categoryReadMapper.map(category);
    }

    public CategoryReadDto findById(Long id) {
        Category checkCategory = categoryRepository.findById(id)
                .orElseThrow(() -> new CategoryNotFoundException("Category with id: " + id + " not found"));
        return categoryReadMapper.map(checkCategory);
    }

    public Page<CategoryReadDto> findAll(Pageable pageable) {
        return categoryRepository.findAll(pageable)
                .map(categoryReadMapper::map);
    }

    public Category getObjectById(Long id) {
        return categoryRepository.findById(id)
                .orElseThrow(() -> new CategoryNotFoundException("Category with id: " + id + " not found"));
    }

    public void delete(Long id) {
        Category category = getObjectById(id);
        category.getProducts().forEach(product -> product.setCategory(null));
        categoryRepository.deleteById(id);
        String successDelete = "Category with id: " + id + " successfully deleted";
        log.info(successDelete);
    }
}
