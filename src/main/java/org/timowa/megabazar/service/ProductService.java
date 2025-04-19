package org.timowa.megabazar.service;

import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;
import org.timowa.megabazar.database.entity.Product;
import org.timowa.megabazar.database.repository.ProductRepository;
import org.timowa.megabazar.dto.product.ProductCreateEditDto;
import org.timowa.megabazar.dto.product.ProductReadDto;
import org.timowa.megabazar.exception.ProductAlreadyExistsException;
import org.timowa.megabazar.exception.ProductNotFoundException;
import org.timowa.megabazar.mapper.product.ProductCreateMapper;
import org.timowa.megabazar.mapper.product.ProductReadMapper;

import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@Validated
public class ProductService {

    private ProductRepository productRepository;
    private ProductReadMapper productReadMapper;
    private ProductCreateMapper productCreateMapper;

    public ProductReadDto findById(Long id) {
        Optional<Product> maybeProduct = productRepository.findById(id);
        if (maybeProduct.isEmpty()) {
            throw new ProductNotFoundException("Product with id " + id + " not found");
        }
        return productReadMapper.map(maybeProduct.get());
    }

    public List<Product> findAll() {
        return productRepository.findAll();
    }

    public ProductReadDto create(@Valid ProductCreateEditDto createDto) {
        if (productRepository.findByName(createDto.getName()).isPresent()) {
            throw new ProductAlreadyExistsException("Product with name: " + createDto.getName() + " already exists");
        }
        Product savedProduct = productRepository.save(productCreateMapper.map(createDto));
        return productReadMapper.map(savedProduct);
    }

    public ProductReadDto addQuantity(Long id, int addQuantity) {
        Optional<Product> maybeProduct = productRepository.findById(id);
        if (maybeProduct.isEmpty()) {
            throw new ProductNotFoundException("Product with id " + id + " not found");
        }
        Product product = maybeProduct.get();
        product.setQuantity(product.getQuantity() + addQuantity);
        Product savedProduct = productRepository.save(product);
        return productReadMapper.map(savedProduct);
    }
}
