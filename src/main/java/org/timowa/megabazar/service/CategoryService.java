package org.timowa.megabazar.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.timowa.megabazar.database.entity.Category;
import org.timowa.megabazar.database.repository.CategoryRepository;
import org.timowa.megabazar.dto.category.CategoryCreateDto;
import org.timowa.megabazar.dto.category.CategoryReadDto;
import org.timowa.megabazar.exception.CategoryAlreadyExistsException;
import org.timowa.megabazar.exception.CategoryNotFoundException;
import org.timowa.megabazar.mapper.category.CategoryCreateMapper;
import org.timowa.megabazar.mapper.category.CategoryReadMapper;

import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class CategoryService {
    private final CategoryRepository categoryRepository;
    private final CategoryCreateMapper categoryCreateMapper;
    private final CategoryReadMapper categoryReadMapper;

    public CategoryReadDto createCategory(CategoryCreateDto createDto) {
        if (categoryRepository.findByName(createDto.getName()).isPresent()) {
            throw new CategoryAlreadyExistsException("Category with name: " + createDto.getName() + " already exists");
        }
        Category savedCategory = categoryRepository.save(categoryCreateMapper.map(createDto));
        return categoryReadMapper.map(savedCategory);
    }

    public List<Category> findAll() {
        return categoryRepository.findAll();
    }

    public List<Category> findAll(Sort sort) {
        return categoryRepository.findAll(sort);
    }

    public Page<Category> findAll(Pageable pageable) {
        return categoryRepository.findAll(pageable);
    }

    public void delete(Long id) {
        if (categoryRepository.findById(id).isEmpty()) {
            throw new CategoryNotFoundException("Category with id: " + id + " not found");
        }
        categoryRepository.deleteById(id);
    }
}
