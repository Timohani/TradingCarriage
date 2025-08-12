package org.timowa.megabazar.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.transaction.annotation.Transactional;
import org.timowa.megabazar.database.entity.Category;
import org.timowa.megabazar.database.entity.Product;
import org.timowa.megabazar.database.entity.Role;
import org.timowa.megabazar.database.entity.User;
import org.timowa.megabazar.database.repository.CategoryRepository;
import org.timowa.megabazar.database.repository.ProductRepository;
import org.timowa.megabazar.dto.category.CategoryCreateDto;
import org.timowa.megabazar.dto.category.CategoryReadDto;
import org.timowa.megabazar.exception.CategoryAlreadyExistsException;
import org.timowa.megabazar.exception.CategoryNotFoundException;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
class CategoryServiceTest {

    @Autowired
    private CategoryService categoryService;

    @Autowired
    private CategoryRepository categoryRepository;

    private Category savedCategoryTest;
    private CategoryCreateDto createDtoToSave;

    @Autowired
    private ProductRepository productRepository;

    @BeforeEach
    void setUp() {
        // For create tests
        createDtoToSave = new CategoryCreateDto("Мебель", "Описание");

    }

    void saveTestCategory() {
        Category testCategory = Category.builder()
                .name("Мебель")
                .description("Описание")
                .build();
        savedCategoryTest = categoryRepository.save(testCategory);
    }

    @Test
    void createCategory() {
        CategoryReadDto createdReadDto = categoryService.createCategory(createDtoToSave);
        CategoryReadDto checkCategory = categoryService.findById(createdReadDto.getId());
        assertNotNull(checkCategory);
        assertEquals(createDtoToSave.getName(), checkCategory.getName());
    }

    @Test
    void createCategory_shouldFail_whenAlreadyExists() {
        saveTestCategory();
        assertThrows(CategoryAlreadyExistsException.class, () -> categoryService.createCategory(createDtoToSave));
    }

    @Test
    void findById() {
        saveTestCategory();
        CategoryReadDto actual = categoryService.findById(savedCategoryTest.getId());
        assertEquals(savedCategoryTest.getName(), actual.getName());
    }

    @Test
    void findById_shouldFail_whenNotFound() {
        saveTestCategory();
        assertThrows(CategoryNotFoundException.class, () -> categoryService.findById(79_993L));
    }

    @Test
    void findAllByPageable() {
        saveTestCategory();
        Pageable sortedByProductsCount =
                PageRequest.of(0, 10, Sort.by("name"));
        Page<CategoryReadDto> page = categoryService.findAll(sortedByProductsCount);
        assertEquals(1, page.getTotalElements());
    }

    @Test
    void delete() {
        saveTestCategory();
        categoryService.delete(savedCategoryTest.getId());
        assertThrows(CategoryNotFoundException.class, () -> categoryService.findById(savedCategoryTest.getId()));
    }

    @Test
    void addProductToCategory() {
        saveTestCategory();
        Product savedProduct = productRepository.save(Product.builder()
                .name("Product")
                .description("Desc")
                .quantity(52)
                .price(993.0)
                .creator(User.builder()
                        .username("test")
                        .email("test@test.test")
                        .password("123456")
                        .role(Role.SELLER)
                        .build())
                .build());
        categoryService.addProductToCategory(savedProduct.getId(), savedCategoryTest);
        assertTrue(savedCategoryTest.getProducts().contains(savedProduct));
    }
}