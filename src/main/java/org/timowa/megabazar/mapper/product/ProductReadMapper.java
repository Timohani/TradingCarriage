package org.timowa.megabazar.mapper.product;

import org.springframework.stereotype.Component;
import org.timowa.megabazar.database.entity.Product;
import org.timowa.megabazar.dto.product.ProductReadDto;
import org.timowa.megabazar.mapper.Mapper;

@Component
public class ProductReadMapper implements Mapper<Product, ProductReadDto> {
    @Override
    public ProductReadDto map(Product product) {
        return new ProductReadDto(
                product.getId(),
                product.getName(),
                product.getDescription(),
                product.getPrice(),
                product.getQuantity(),
                product.getCategory() != null ? product.getCategory().getName() : null,
                product.getQuantity() >= 1,
                product.getCreator().getUsername()
        );
    }
}
