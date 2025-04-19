package org.timowa.megabazar.mapper.product;

import org.timowa.megabazar.database.entity.Product;
import org.timowa.megabazar.dto.product.ProductReadDto;
import org.timowa.megabazar.mapper.Mapper;

public class ProductReadMapper implements Mapper<Product, ProductReadDto> {
    @Override
    public ProductReadDto map(Product product) {
        return new ProductReadDto(
                product.getId(),
                product.getName(),
                product.getDescription(),
                product.getPrice(),
                product.getQuantity(),
                product.getCategory()
        );
    }
}
