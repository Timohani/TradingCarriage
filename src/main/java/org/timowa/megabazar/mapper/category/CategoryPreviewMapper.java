package org.timowa.megabazar.mapper.category;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.timowa.megabazar.database.entity.Category;
import org.timowa.megabazar.dto.category.CategoryPreviewDto;
import org.timowa.megabazar.mapper.Mapper;

@Component
@RequiredArgsConstructor
public class CategoryPreviewMapper implements Mapper<Category, CategoryPreviewDto> {

    @Override
    public CategoryPreviewDto map(Category category) {
        return new CategoryPreviewDto(
                category.getId(),
                category.getName(),
                category.getDescription()
        );
    }
}
