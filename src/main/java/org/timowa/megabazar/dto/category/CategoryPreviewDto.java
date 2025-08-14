package org.timowa.megabazar.dto.category;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class CategoryPreviewDto {
    private Long id;

    private String name;

    private String description;
}
