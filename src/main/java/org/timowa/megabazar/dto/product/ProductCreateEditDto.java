package org.timowa.megabazar.dto.product;

import lombok.AllArgsConstructor;
import lombok.Getter;
import jakarta.validation.constraints.*;
import org.timowa.megabazar.database.entity.Category;

@Getter
@AllArgsConstructor
public class ProductCreateEditDto {
    @NotEmpty
    @NotNull
    private String name;

    private String description;

    @NotNull
    @DecimalMin(value = "1.0")
    private double price;

    @NotNull
    @Min(value = 1)
    private int quantity;

    private Category category;

    private String creator;
}
