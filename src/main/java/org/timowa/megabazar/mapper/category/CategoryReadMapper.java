package org.timowa.megabazar.mapper.category;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.timowa.megabazar.database.entity.Category;
import org.timowa.megabazar.dto.category.CategoryReadDto;
import org.timowa.megabazar.mapper.Mapper;
import org.timowa.megabazar.mapper.product.ProductReadMapper;

@Component
@RequiredArgsConstructor
public class CategoryReadMapper implements Mapper<Category, CategoryReadDto> {

    private final ProductReadMapper productReadMapper;

    @Override
    public CategoryReadDto map(Category category) {
        return new CategoryReadDto(
                category.getId(),
                category.getName(),
                category.getDescription(),
                category.getProducts().stream().map(productReadMapper::map).toList()
        );
    }
}
