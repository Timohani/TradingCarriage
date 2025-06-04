package org.timowa.megabazar.mapper.product;

import org.springframework.stereotype.Component;
import org.timowa.megabazar.database.entity.Product;
import org.timowa.megabazar.dto.product.ProductListReadDto;
import org.timowa.megabazar.mapper.Mapper;

@Component
public class ProductListReadMapper implements Mapper<Product, ProductListReadDto> {
    @Override
    public ProductListReadDto map(Product fromObject) {
        return new ProductListReadDto(
                fromObject.getName(),
                fromObject.getPrice(),
                fromObject.getQuantity(),
                fromObject.isAvailable()
        );
    }
}
