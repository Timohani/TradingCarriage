package org.timowa.megabazar.dto.product;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Getter
public class ProductListReadDto {

    private String name;

    private double price;

    private int quantity;

    private boolean isAvailable;
}
