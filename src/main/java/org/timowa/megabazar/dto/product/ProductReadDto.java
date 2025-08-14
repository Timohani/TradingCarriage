package org.timowa.megabazar.dto.product;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Getter
public class ProductReadDto {

    private Long id;

    private String name;

    private String description;

    private double price;

    private int quantity;

    private String categoryName;

    private boolean isAvailable;

    private String creator;
}
