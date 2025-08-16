package org.timowa.megabazar.service;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import org.timowa.megabazar.database.entity.Product;
import org.timowa.megabazar.database.entity.Role;
import org.timowa.megabazar.database.entity.User;
import org.timowa.megabazar.database.repository.ProductRepository;
import org.timowa.megabazar.database.repository.UserRepository;
import org.timowa.megabazar.exception.CartLimitExceededException;
import org.timowa.megabazar.exception.ProductNotAvailableException;

import java.time.LocalDateTime;
import java.util.List;

@SpringBootTest
@Transactional
public class OrderServiceTest {
    @Autowired
    private OrderService orderService;
    @Autowired
    private LoginContext loginContext;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private CartService cartService;
    @Autowired
    private ProductRepository productRepository;

    @Test
    void test() throws CartLimitExceededException, ProductNotAvailableException {
        User user = User.builder()
                .id(993L)
                .username("adfjkls")
                .password("feqhfiyqw4")
                .email("aeorw@agiopsuvh.adpvwh")
                .role(Role.SELLER)
                .createdAt(LocalDateTime.now())
                .money(0.0)
                .build();
        userRepository.save(user);
        loginContext.setLoginUser(user);
        Product product = Product.builder()
                .name("gpaweh")
                .description("goquw4y")
                .price(1345678.0)
                .quantity(1578)
                .createdAt(LocalDateTime.now())
                .creator(user)
                .build();
        Product savedProduct = productRepository.save(product);

        cartService.addItemToCart(savedProduct.getId());


        orderService.createOrder(List.of(2L, 3L));
    }
}
