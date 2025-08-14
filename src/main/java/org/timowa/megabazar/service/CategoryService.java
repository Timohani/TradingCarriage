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
import org.timowa.megabazar.dto.category.CategoryPreviewDto;
import org.timowa.megabazar.dto.category.CategoryReadDto;
import org.timowa.megabazar.dto.product.ProductReadDto;
import org.timowa.megabazar.exception.CategoryAlreadyExistsException;
import org.timowa.megabazar.exception.CategoryNotFoundException;
import org.timowa.megabazar.mapper.category.CategoryCreateMapper;
import org.timowa.megabazar.mapper.category.CategoryPreviewMapper;
import org.timowa.megabazar.mapper.category.CategoryReadMapper;
import org.timowa.megabazar.mapper.product.ProductReadMapper;

@Slf4j
@Service
@Validated
@Transactional
@RequiredArgsConstructor
public class CategoryService {
    private final CategoryRepository categoryRepository;
    private final CategoryCreateMapper categoryCreateMapper;
    private final CategoryReadMapper categoryReadMapper;
    private final CategoryPreviewMapper categoryPreviewMapper;
    private final ProductReadMapper productReadMapper;
    private final ProductService productService;

    public CategoryReadDto createCategory(@Valid CategoryCreateDto createDto) {
        if (categoryRepository.findByName(createDto.getName()).isPresent()) {
            throw new CategoryAlreadyExistsException("Category with name: " + createDto.getName() + " already exists");
        }
        Category savedCategory = categoryRepository.save(categoryCreateMapper.map(createDto));
        return categoryReadMapper.map(savedCategory);
    }

    public ProductReadDto addProductToCategory(Long productId, Long categoryId) {
        Product product = productService.getObjectById(productId);
        Category category = getObjectById(categoryId);
        if (product.getCategory() == null) {
            product.setCategory(category);
            category.getProducts().add(product);
        } else {
            throw new IllegalArgumentException("Product already has category");
        }
        return productReadMapper.map(product);
    }

    public ProductReadDto removeProductFromCategory(Long productId, Long categoryId) {
        Product product = productService.getObjectById(productId);
        Category category = getObjectById(categoryId);
        if (product.getCategory() != null) {
            product.setCategory(null);
            category.getProducts().remove(product);
        } else {
            throw new IllegalArgumentException("Product already has no category");
        }
        return productReadMapper.map(product);
    }

    public CategoryReadDto findById(Long id) {
        Category checkCategory = categoryRepository.findById(id)
                .orElseThrow(() -> new CategoryNotFoundException("Category with id: " + id + " not found"));
        return categoryReadMapper.map(checkCategory);
    }

    public Page<CategoryPreviewDto> findAll(Pageable pageable) {
        return categoryRepository.findAll(pageable)
                .map(categoryPreviewMapper::map);
    }

    Category getObjectById(Long id) {
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
