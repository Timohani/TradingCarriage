package org.timowa.megabazar.dto.category;

import lombok.Getter;
import org.timowa.megabazar.database.entity.Product;

import java.util.List;

@Getter
public class CategoryCreateDto {

    private String name;

    private String description;

    private List<Product> products;
}
