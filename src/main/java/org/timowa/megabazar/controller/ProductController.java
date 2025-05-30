package org.timowa.megabazar.controller;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PagedModel;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import org.timowa.megabazar.database.entity.Product;
import org.timowa.megabazar.database.repository.ProductRepository;
import org.timowa.megabazar.dto.product.ProductCreateEditDto;
import org.timowa.megabazar.dto.product.ProductReadDto;
import org.timowa.megabazar.service.ProductService;

import java.io.IOException;
import java.util.Collection;
import java.util.List;

@RestController
@RequestMapping("/api/products")
@RequiredArgsConstructor
public class ProductController {

    private final ProductRepository productRepository;
    private final ProductService productService;

    private final ObjectMapper objectMapper;

    // TODO: fix loop bug
    @GetMapping
    public PagedModel<Product> getAll(@ParameterObject Pageable pageable) {
        Page<Product> products = productRepository.findAll(pageable);
        return new PagedModel<>(products);
    }

    @GetMapping("/{id}")
    public ProductReadDto getOne(@PathVariable Long id) {
        return productService.findById(id);
    }

    @GetMapping("/by-ids")
    public List<Product> getMany(@RequestParam List<Long> ids) {
        return productRepository.findAllById(ids);
    }

    @PostMapping
    public ProductReadDto create(@RequestBody ProductCreateEditDto createDto) {
        return productService.create(createDto);
    }

    @PatchMapping("/{id}")
    public Product patch(@PathVariable Long id, @RequestBody JsonNode patchNode) throws IOException {
        Product product = productRepository.findById(id).orElseThrow(() ->
                new ResponseStatusException(HttpStatus.NOT_FOUND, "Entity with id `%s` not found".formatted(id)));

        objectMapper.readerForUpdating(product).readValue(patchNode);

        return productRepository.save(product);
    }

    @PatchMapping
    public List<Long> patchMany(@RequestParam List<Long> ids, @RequestBody JsonNode patchNode) throws IOException {
        Collection<Product> products = productRepository.findAllById(ids);

        for (Product product : products) {
            objectMapper.readerForUpdating(product).readValue(patchNode);
        }

        List<Product> resultProducts = productRepository.saveAll(products);
        return resultProducts.stream()
                .map(Product::getId)
                .toList();
    }

    @DeleteMapping("/{id}")
    public Product delete(@PathVariable Long id) {
        Product product = productRepository.findById(id).orElse(null);
        if (product != null) {
            productRepository.delete(product);
        }
        return product;
    }

    @DeleteMapping
    public void deleteMany(@RequestParam List<Long> ids) {
        productRepository.deleteAllById(ids);
    }
}
