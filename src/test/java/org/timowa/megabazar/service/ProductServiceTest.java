package org.timowa.megabazar.service;

import jakarta.validation.ConstraintViolationException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import org.timowa.megabazar.dto.product.ProductCreateEditDto;
import org.timowa.megabazar.dto.product.ProductReadDto;
import org.timowa.megabazar.exception.ProductAlreadyExistsException;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
class ProductServiceTest {

    @Autowired
    private ProductService productService;

    @Test
    void create() {
        ProductCreateEditDto invalidDto = new ProductCreateEditDto(
                "",
                "Из китая",
                -12.6,
                0,
                null
        );
        assertThrows(ConstraintViolationException.class, () -> productService.create(invalidDto));

        ProductCreateEditDto validDto = new ProductCreateEditDto(
                "Фен",
                "Хороший фен, всё высушит",
                39999.99,
                52,
                null
        );
        ProductReadDto expected = productService.create(validDto);
        assertEquals(expected.getName(),validDto.getName());

        ProductCreateEditDto sameValidDto = new ProductCreateEditDto(
                "Фен",
                "Другой фен, с таким же названием",
                2357.3,
                5,
                null
        );
        assertThrows(ProductAlreadyExistsException.class, () -> productService.create(sameValidDto));
    }

    @Test
    void addQuantity() {
        ProductCreateEditDto dto = new ProductCreateEditDto(
                "Фен",
                "Хороший фен, всё высушит",
                39999.99,
                52,
                null
        );
        ProductReadDto savedDto = productService.create(dto);
        ProductReadDto updatedDto = productService.addQuantity(savedDto.getId(),27);
        assertEquals(79, updatedDto.getQuantity());
    }
}