package org.timowa.megabazar.service;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;
import org.timowa.megabazar.database.entity.Product;
import org.timowa.megabazar.database.entity.User;
import org.timowa.megabazar.database.repository.ProductRepository;
import org.timowa.megabazar.dto.product.ProductCreateEditDto;
import org.timowa.megabazar.dto.product.ProductListReadDto;
import org.timowa.megabazar.dto.product.ProductReadDto;
import org.timowa.megabazar.exception.ProductAlreadyExistsException;
import org.timowa.megabazar.exception.ProductNotFoundException;
import org.timowa.megabazar.exception.UserNotFoundException;
import org.timowa.megabazar.mapper.product.ProductCreateMapper;
import org.timowa.megabazar.mapper.product.ProductListReadMapper;
import org.timowa.megabazar.mapper.product.ProductReadMapper;

import java.util.Optional;

@Service
@Validated
@RequiredArgsConstructor
@Transactional
public class ProductService {

    private final ProductRepository productRepository;
    private final UserService userService;

    private final ProductReadMapper productReadMapper;
    private final ProductCreateMapper productCreateMapper;
    private final ProductListReadMapper productListReadMapper;

    public Page<ProductListReadDto> findAll(Pageable pageable) {
        return productRepository.findAll(pageable).map(productListReadMapper::map);
    }

    public ProductReadDto findById(Long id) {
        Optional<Product> maybeProduct = productRepository.findById(id);
        if (maybeProduct.isEmpty()) {
            throw new ProductNotFoundException("Product with id " + id + " not found");
        }
        return productReadMapper.map(maybeProduct.get());
    }

    public ProductReadDto create(@Valid ProductCreateEditDto createDto) {
        if (productRepository.findByName(createDto.getName()).isPresent()) {
            throw new ProductAlreadyExistsException("Product with name: " + createDto.getName() + " already exists");
        }
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        User user = userService.getUserByUsername(username);
        Product savedProduct = productRepository.save(productCreateMapper.map(createDto, user));
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

    public void delete(Long id) {
        Long userId = userService.getLoginUser().getId();
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ProductNotFoundException("Product with id: " + id + "not found"));
        if (product.getCreator().getId().equals(userId)) {
            productRepository.delete(product);
            return;
        }
        throw new UserNotFoundException("User not found");
    }
}
