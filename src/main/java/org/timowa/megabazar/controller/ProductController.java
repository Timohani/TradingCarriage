package org.timowa.megabazar.controller;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.apache.tomcat.websocket.AuthenticationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PagedModel;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import org.timowa.megabazar.database.entity.Product;
import org.timowa.megabazar.database.repository.ProductRepository;
import org.timowa.megabazar.dto.cartItem.CartItemReadDto;
import org.timowa.megabazar.dto.product.ProductCreateEditDto;
import org.timowa.megabazar.dto.product.ProductReadDto;
import org.timowa.megabazar.exception.CartLimitExceededException;
import org.timowa.megabazar.exception.ProductNotAvailableException;
import org.timowa.megabazar.service.CartService;
import org.timowa.megabazar.service.ProductService;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/products")
@RequiredArgsConstructor
public class ProductController {

    private final ProductRepository productRepository;
    private final ProductService productService;
    private final CartService cartService;

    private final ObjectMapper objectMapper;

    @GetMapping
    public PagedModel<ProductReadDto> getAll(Pageable pageable) {
        Page<ProductReadDto> products = productService.findAll(pageable);
        return new PagedModel<>(products);
    }

    @GetMapping("/{id}")
    public ProductReadDto getOne(@PathVariable Long id) {
        return productService.findById(id);
    }

    @PostMapping
    public ProductReadDto create(@RequestBody ProductCreateEditDto createDto) {
        return productService.create(createDto);
    }

    @PostMapping("/{id}/addToCart")
    public ResponseEntity<CartItemReadDto> addToCart(@PathVariable Long id) throws CartLimitExceededException, ProductNotAvailableException {
        return ResponseEntity.of(Optional.of(cartService.addItemToCart(id)));
    }

    @PatchMapping("/{id}")
    public Product patch(@PathVariable Long id, @RequestBody JsonNode patchNode) throws IOException {
        Product product = productRepository.findById(id).orElseThrow(() ->
                new ResponseStatusException(HttpStatus.NOT_FOUND, "Entity with id `%s` not found".formatted(id)));

        objectMapper.readerForUpdating(product).readValue(patchNode);

        return productRepository.save(product);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> delete(@PathVariable Long id) {
        try {
            productService.delete(id);
        } catch (AuthenticationException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
        return ResponseEntity.ok().build();
    }

    @DeleteMapping
    public ResponseEntity<String> deleteMany(@RequestParam List<Long> ids) {
        try {
            productService.deleteMany(ids);
        } catch (AuthenticationException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
        return ResponseEntity.ok().build();
    }

    @PostMapping("/{id}")
    public ProductReadDto addQuantity(@PathVariable Long id, @RequestParam Integer addAmount) {
        return productService.addQuantity(id, addAmount);
    }
}
