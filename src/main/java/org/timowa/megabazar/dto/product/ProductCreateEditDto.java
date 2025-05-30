package org.timowa.megabazar.dto.product;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
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

    private String creator;

    public ProductCreateEditDto(String name, String description, double price, int quantity) {
        this.name = name;
        this.description = description;
        this.price = price;
        this.quantity = quantity;
    }
}
