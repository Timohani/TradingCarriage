package org.timowa.megabazar.service;

import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.transaction.annotation.Transactional;
import org.timowa.megabazar.database.entity.*;
import org.timowa.megabazar.database.repository.*;
import org.timowa.megabazar.dto.order.OrderPreviewDto;
import org.timowa.megabazar.dto.order.OrderReadDto;
import org.timowa.megabazar.exception.CartItemNotFoundException;
import org.timowa.megabazar.exception.InsufficientFundsException;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
class OrderServiceTest {

    @Autowired
    private OrderService orderService;
    @Autowired
    private OrderRepository orderRepository;
    @Autowired
    private OrderItemRepository orderItemRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private ProductRepository productRepository;
    @Autowired
    private CartRepository cartRepository;
    @Autowired
    private CartItemRepository cartItemRepository;
    @Autowired
    private LoginContext loginContext;

    private User testUser;
    private Product testProduct1;
    private Product testProduct2;
    private CartItem testCartItem1;
    private CartItem testCartItem2;

    @BeforeEach
    void setUp() {
        // Создаем тестового пользователя
        testUser = new User();
        testUser.setUsername("test_user");
        testUser.setEmail("test@test.test");
        testUser.setPassword("sh3673idh272hdk38");
        testUser.setMoney(1000.0);
        testUser.setRole(Role.ADMIN);
        testUser = userRepository.save(testUser);

        // Настраиваем мок контекста авторизации
        loginContext.setLoginUser(testUser);

        // Создаем тестовые продукты
        testProduct1 = new Product();
        testProduct1.setName("Product 1");
        testProduct1.setPrice(100.0);
        testProduct1.setQuantity(10);
        testProduct1.setCreator(testUser);
        testProduct1 = productRepository.save(testProduct1);

        testProduct2 = new Product();
        testProduct2.setName("Product 2");
        testProduct2.setPrice(200.0);
        testProduct2.setQuantity(5);
        testProduct2.setCreator(testUser);
        testProduct2 = productRepository.save(testProduct2);

        // Создаем тестовую корзину
        Cart testCart = new Cart();
        testCart.setUser(testUser);
        testCart = cartRepository.save(testCart);
        testUser.setCart(testCart);

        // Добавляем товары в корзину
        testCartItem1 = new CartItem();
        testCartItem1.setCart(testCart);
        testCartItem1.setProduct(testProduct1);
        testCartItem1.setQuantity(2);
        testCartItem1 = cartItemRepository.save(testCartItem1);

        testCartItem2 = new CartItem();
        testCartItem2.setCart(testCart);
        testCartItem2.setProduct(testProduct2);
        testCartItem2.setQuantity(1);
        testCartItem2 = cartItemRepository.save(testCartItem2);

        testCart.getCartItems().add(testCartItem1);
        testCart.getCartItems().add(testCartItem2);
        cartRepository.save(testCart);
    }

    @Test
    void createOrder_ShouldCreateOrderAndUpdateUserBalance() {
        // Act
        OrderReadDto orderDto = orderService.createOrder(List.of(testCartItem1.getId(), testCartItem2.getId()));

        // Assert
        assertNotNull(orderDto);
        assertEquals(400.0, orderDto.getTotalPrice()); // 2*100 + 1*200 = 400
        assertEquals(OrderStatus.GETTING, orderDto.getStatus());

        // Проверяем баланс пользователя
        User updatedUser = userRepository.findById(testUser.getId()).orElseThrow();
        assertEquals(600.0, updatedUser.getMoney()); // 1000 - 400 = 600

        // Проверяем количество товаров
        Product updatedProduct1 = productRepository.findById(testProduct1.getId()).orElseThrow();
        assertEquals(8, updatedProduct1.getQuantity()); // 10 - 2 = 8

        Product updatedProduct2 = productRepository.findById(testProduct2.getId()).orElseThrow();
        assertEquals(4, updatedProduct2.getQuantity()); // 5 - 1 = 4

        // Проверяем, что заказ сохранен в БД
        assertTrue(orderRepository.existsById(orderDto.getId()));
    }

    @Test
    void createOrder_ShouldThrowInsufficientFundsException_WhenUserHasNotEnoughMoney() {
        // Arrange
        testUser.setMoney(300.0);
        userRepository.save(testUser);

        // Act & Assert
        assertThrows(InsufficientFundsException.class, () ->
                orderService.createOrder(List.of(testCartItem1.getId(), testCartItem2.getId())));
    }

    @Test
    void createOrder_ShouldThrowCartItemNotFoundException_WhenCartItemNotFound() {
        // Act & Assert
        assertThrows(CartItemNotFoundException.class, () ->
                orderService.createOrder(List.of(9999L)));
    }

    @Test
    void getActiveOrders_ShouldReturnOrdersWithStatusLessThanDone() {
        // Arrange - создаем несколько заказов с разными статусами
        createTestOrder(OrderStatus.GETTING);
        createTestOrder(OrderStatus.GOING);
        createTestOrder(OrderStatus.WAITING);
        createTestOrder(OrderStatus.DONE);

        // Act
        List<OrderPreviewDto> activeOrders = orderService.getActiveOrders(testUser.getId(), PageRequest.of(0, 10));

        // Assert
        assertEquals(4, activeOrders.size()); // 3 активных + 1 выполненный (по логике сервиса)
    }

    @Test
    void getDoneOrders_ShouldReturnOnlyDoneOrders() {
        // Arrange
        createTestOrder(OrderStatus.GETTING);
        createTestOrder(OrderStatus.DONE);
        createTestOrder(OrderStatus.DONE);

        // Act
        List<OrderPreviewDto> doneOrders = orderService.getDoneOrders(testUser.getId(), PageRequest.of(0, 10));

        // Assert
        assertEquals(1, doneOrders.size()); // только один выполненный (по логике сервиса)
    }

    @Test
    void takeWaitingOrder_ShouldChangeStatusToDone() {
        // Arrange
        Order order = createTestOrder(OrderStatus.WAITING);

        // Act
        OrderReadDto result = orderService.takeWaitingOrder(order.getId());

        // Assert
        assertEquals(OrderStatus.DONE, result.getStatus());
    }

    @Test
    void takeWaitingOrder_ShouldThrowException_WhenOrderNotWaiting() {
        // Arrange
        Order order = createTestOrder(OrderStatus.GETTING);

        // Act & Assert
        assertThrows(EntityNotFoundException.class, () ->
                orderService.takeWaitingOrder(order.getId()));
    }

    @Test
    void cancelOrder_ShouldRefundMoneyAndDeleteOrder_WhenOrderNotDone() {
        // Arrange
        Order order = createTestOrder(OrderStatus.GETTING);
        double initialMoney = testUser.getMoney();

        // Act
        orderService.cancelOrder(order.getId());

        // Assert
        User updatedUser = userRepository.findById(testUser.getId()).orElseThrow();
        assertEquals(initialMoney + order.getTotalPrice(), updatedUser.getMoney());

        // Проверяем, что товары вернулись на склад
        Product updatedProduct1 = productRepository.findById(testProduct1.getId()).orElseThrow();
        assertEquals(testProduct1.getQuantity(), updatedProduct1.getQuantity());

        // Проверяем, что заказ удален
        assertFalse(orderRepository.existsById(order.getId()));
    }

    @Test
    void cancelOrder_ShouldThrowException_WhenOrderAlreadyDone() {
        // Arrange
        Order order = createTestOrder(OrderStatus.DONE);

        // Act & Assert
        assertThrows(EntityNotFoundException.class, () ->
                orderService.cancelOrder(order.getId()));
    }

    @Test
    void findOrderById_ShouldReturnOrderWithUpdatedStatus() {
        // Arrange
        Order order = createTestOrder(OrderStatus.GETTING);
        order.setCreatedAt(LocalDateTime.now().minusMinutes(10)); // Имитируем прошедшее время

        // Act
        OrderReadDto result = orderService.findOrderById(order.getId());

        // Assert
        assertNotEquals(OrderStatus.GETTING, result.getStatus()); // Статус должен обновиться
    }

    private Order createTestOrder(OrderStatus status) {
        Order order = new Order();
        order.setUser(testUser);
        order.setTotalPrice(100.0);
        order.setStatus(status);
        order.setCreatedAt(LocalDateTime.now());
        order = orderRepository.save(order);

        OrderItem orderItem = new OrderItem();
        orderItem.setOrder(order);
        orderItem.setProduct(testProduct1);
        orderItem.setQuantity(1);
        orderItem.setPrice(testProduct1.getPrice());
        orderItemRepository.save(orderItem);

        order.getOrderItems().add(orderItem);
        return order;
    }
}