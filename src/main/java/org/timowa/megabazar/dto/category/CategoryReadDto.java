package org.timowa.megabazar.dto.category;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.timowa.megabazar.database.entity.Product;

import java.util.List;

@Getter
@AllArgsConstructor
public class CategoryReadDto {

    private Long id;

    private String name;

    private String description;

    private List<Product> products;
}
