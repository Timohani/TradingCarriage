package org.timowa.megabazar.mapper.product;

import org.springframework.stereotype.Component;
import org.timowa.megabazar.database.entity.Product;
import org.timowa.megabazar.dto.product.ProductCreateEditDto;
import org.timowa.megabazar.mapper.Mapper;

@Component
public class ProductCreateMapper implements Mapper<ProductCreateEditDto, Product> {
    @Override
    public Product map(ProductCreateEditDto createEditDto) {
        Product product = new Product();
        product.setName(createEditDto.getName());
        product.setDescription(createEditDto.getDescription());
        product.setPrice(createEditDto.getPrice());
        product.setQuantity(createEditDto.getQuantity());
        product.setCategory(createEditDto.getCategory());
        return product;
    }
}
