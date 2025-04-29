package org.timowa.megabazar.dto.category;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class CategoryCreateDto {

    @NotNull
    @NotEmpty
    private String name;

    private String description;
}
