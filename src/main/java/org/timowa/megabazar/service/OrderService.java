package org.timowa.megabazar.service;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.timowa.megabazar.database.entity.*;
import org.timowa.megabazar.database.repository.CartRepository;
import org.timowa.megabazar.database.repository.OrderItemRepository;
import org.timowa.megabazar.database.repository.OrderRepository;
import org.timowa.megabazar.dto.order.OrderPreviewDto;
import org.timowa.megabazar.dto.order.OrderReadDto;
import org.timowa.megabazar.exception.CartItemNotFoundException;
import org.timowa.megabazar.exception.InsufficientFundsException;
import org.timowa.megabazar.mapper.order.OrderPreviewMapper;
import org.timowa.megabazar.mapper.order.OrderReadMapper;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;


@Service
@RequiredArgsConstructor
@Transactional
public class OrderService {

    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;
    private final CartService cartService;
    private final CartRepository cartRepository;
    private final LoginContext loginContext;
    private final UserService userService;

    private final OrderPreviewMapper orderPreviewMapper;
    private final OrderReadMapper orderReadMapper;

    public List<OrderPreviewDto> getActiveOrders(Long userId, Pageable pageable) {
        updateStatus(userId, pageable);
        // Ищутся все заказы которые имеют статусы: GETTING, GOING, WAITING
        List<OrderPreviewDto> orders = new ArrayList<>();
        orderRepository.findAllByUserId(userId, pageable).stream()
                .filter(order -> order.getStatus().ordinal() < 3)
                .map(orderPreviewMapper::map)
                .forEach(orders::add);
        // Добавляется один элемент со статусом DONE
        orderRepository.findAllByUserId(userId, pageable).stream()
                .filter(order -> order.getStatus().ordinal() == 3)
                .map(orderPreviewMapper::map)
                .findFirst().ifPresent(orders::add);
        return orders;
    }

    public List<OrderPreviewDto> getDoneOrders(Long userId, Pageable pageable) {
        updateStatus(userId, pageable);
        // Ищутся остальные заказы
        return orderRepository.findAllByUserId(userId, pageable).stream()
                .filter(order -> order.getStatus().ordinal() == 3)
                .map(orderPreviewMapper::map)
                .skip(1)
                .toList();
    }

    public OrderReadDto takeWaitingOrder(Long id) {
        Order order = getObjectById(id);
        if (order.getStatus().ordinal() < 2) {
            throw new EntityNotFoundException("Order not yet arrived");
        }
        if (order.getStatus().ordinal() == 3) {
            throw new EntityNotFoundException("Order already taken");
        }
        order.setStatus(OrderStatus.DONE);
        return orderReadMapper.map(order);

    }

    private void updateStatus(Long id) {
        Order order = getObjectById(id);
        checkTime(order);
    }

    private void checkTime(Order order) {
        // Имитация ожидания
        if (LocalDateTime.now().getMinute() >= order.getCreatedAt().getMinute() + (order.getStatus().ordinal() + 1) * 5) {
            if (order.getStatus().ordinal() == 0) {
                order.setStatus(OrderStatus.GOING);
            } else if (order.getStatus().ordinal() == 1) {
                order.setStatus(OrderStatus.WAITING);
            }
        }
    }

    private void updateStatus(Long userId, Pageable pageable) {
        List<Order> orders = orderRepository.findAllByUserId(userId, pageable).stream()
                .filter(order -> order.getStatus().ordinal() < 2)
                .toList();
        for (Order order : orders) {
            checkTime(order);
        }
    }

    Order getObjectById(Long id) {
        return orderRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Order with id: " + id + " doesn't exists"));
    }

    public OrderReadDto findOrderById(Long id) {
        updateStatus(id);
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Order with id: " + id + " doesn't exists"));
        return orderReadMapper.map(order);
    }

    public OrderReadDto createOrder(List<Long> cartItemIds) {
        // Добавление корзины в сессию
        List<CartItem> cartItemsToOrder = new ArrayList<>();
        Cart cart = cartService.getCurrentUserCart();
        Cart persistCart = cartRepository.findById(cart.getId())
                .orElseThrow(() -> new CartItemNotFoundException("Cart not found"));
        // Сортировка только выбранных товаров в корзине
        persistCart.getCartItems()
                .stream()
                .filter(cartItem -> cartItemIds.contains(cartItem.getId()))
                .forEach(cartItemsToOrder::add);
        if (cartItemsToOrder.isEmpty()) {
            throw new CartItemNotFoundException("Cart item not found");
        }
        // Подсчёт итоговой суммы
        User loginUser = userService.getObjectByUsername(loginContext.getLoginUser().getUsername());
        double totalPrice = 0;
        for (CartItem cartItem : cartItemsToOrder) {
            totalPrice += cartItem.getProduct().getPrice() * cartItem.getQuantity();
            // Проверка количества товара
            if (cartItem.getProduct().getQuantity() < cartItem.getQuantity()) {
                cartItem.setQuantity(cartItem.getProduct().getQuantity());
            }
        }
        // Проверяется если у пользователя недостаточно средств и снимаются деньги со счёта
        if (totalPrice > loginUser.getMoney()) {
            throw new InsufficientFundsException("Insufficient Funds to create an order. Price: " + totalPrice + ", Money: " + loginUser.getMoney());
        }
        loginUser.setMoney(loginUser.getMoney() - totalPrice);
        // Создание заказа
        Order order = new Order();
        order.setUser(loginUser);
        order.setTotalPrice(totalPrice);
        order.setStatus(OrderStatus.GETTING);
        order.setCreatedAt(LocalDateTime.now());
        Order savedOrder = orderRepository.save(order);
        for (CartItem cartItem : cartItemsToOrder) {
            OrderItem orderItem = OrderItem.builder()
                    .product(cartItem.getProduct())
                    .quantity(cartItem.getQuantity())
                    .price(cartItem.getProduct().getPrice())
                    .build();
            savedOrder.getOrderItems().add(orderItem);
            orderItem.setOrder(savedOrder);
            orderItemRepository.save(orderItem);
            // Взятие количества у продукта
            Product product = orderItem.getProduct();
            product.setQuantity(product.getQuantity() - orderItem.getQuantity());
        }
        return orderReadMapper.map(savedOrder);
    }

    public void cancelOrder(Long id) {
        updateStatus(id);
        Order order = getObjectById(id);
        if (order.getStatus().ordinal() != 3) {
            User loginUser = userService.getObjectByUsername(loginContext.getLoginUser().getUsername());
            loginUser.setMoney(loginUser.getMoney() + order.getTotalPrice());
            for (OrderItem orderItem : order.getOrderItems()) {
                Product product = orderItem.getProduct();
                product.setQuantity(product.getQuantity() + orderItem.getQuantity());
            }
            orderRepository.delete(order);
            return;
        }
        throw new EntityNotFoundException("Order has already arrived and taken");
    }
}
