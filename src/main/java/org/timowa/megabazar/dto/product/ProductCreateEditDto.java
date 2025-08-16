package org.timowa.megabazar.dto.product;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
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
}
