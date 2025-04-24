package org.timowa.megabazar.mapper.category;

import org.springframework.stereotype.Component;
import org.timowa.megabazar.database.entity.Category;
import org.timowa.megabazar.dto.category.CategoryReadDto;
import org.timowa.megabazar.mapper.Mapper;

@Component
public class CategoryReadMapper implements Mapper<Category, CategoryReadDto> {
    @Override
    public CategoryReadDto map(Category category) {
        return new CategoryReadDto(
                category.getId(),
                category.getName(),
                category.getDescription(),
                category.getProducts()
        );
    }
}
