package org.timowa.megabazar.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PagedModel;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.timowa.megabazar.dto.category.CategoryCreateDto;
import org.timowa.megabazar.dto.category.CategoryPreviewDto;
import org.timowa.megabazar.dto.category.CategoryReadDto;
import org.timowa.megabazar.dto.product.ProductReadDto;
import org.timowa.megabazar.service.CategoryService;

@RestController
@RequestMapping("/api/category")
@RequiredArgsConstructor
public class CategoryController {
    public final CategoryService categoryService;

    @PostMapping
    public CategoryReadDto createCategory(CategoryCreateDto createDto) {
        return categoryService.createCategory(createDto);
    }

    @PostMapping("/product/{productId}")
    public ProductReadDto addProductToCategory(@PathVariable Long productId, Long categoryId) {
        return categoryService.addProductToCategory(productId, categoryId);
    }

    @DeleteMapping("/product/{productId}")
    public ProductReadDto removeProductFromCategory(@PathVariable Long productId, Long categoryId) {
        return categoryService.removeProductFromCategory(productId, categoryId);
    }

    @GetMapping("/{id}")
    public CategoryReadDto findById(@PathVariable Long id) {
        return categoryService.findById(id);
    }

    @GetMapping
    public PagedModel<CategoryPreviewDto> findAll(Pageable pageable) {
        Page<CategoryPreviewDto> categories = categoryService.findAll(pageable);
        return new PagedModel<>(categories);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable Long id) {
        categoryService.delete(id);
        return ResponseEntity.ok().body(null);
    }
}
