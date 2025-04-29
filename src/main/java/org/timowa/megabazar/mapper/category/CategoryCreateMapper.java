package org.timowa.megabazar.mapper.category;

import org.springframework.stereotype.Component;
import org.timowa.megabazar.database.entity.Category;
import org.timowa.megabazar.dto.category.CategoryCreateDto;
import org.timowa.megabazar.mapper.Mapper;

@Component
public class CategoryCreateMapper implements Mapper<CategoryCreateDto, Category> {
    @Override
    public Category map(CategoryCreateDto createDto) {
        Category category = new Category();
        category.setName(createDto.getName());
        category.setDescription(createDto.getDescription());
        return category;
    }
}
