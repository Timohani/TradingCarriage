package org.timowa.megabazar.controller;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PagedModel;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.timowa.megabazar.dto.order.OrderPreviewDto;
import org.timowa.megabazar.dto.order.OrderReadDto;
import org.timowa.megabazar.service.LoginContext;
import org.timowa.megabazar.service.OrderService;
import org.timowa.megabazar.service.UserService;

import java.util.List;

@RestController
@RequestMapping("/api/order")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;
    private final LoginContext loginContext;
    private final UserService userService;

    @GetMapping("/active")
    public PagedModel<OrderPreviewDto> getActiveOrders(Pageable pageable) {
        Long userId = userService.getObjectByUsername(loginContext.getLoginUser().getUsername()).getId();
        List<OrderPreviewDto> orders = orderService.getActiveOrders(userId, pageable);
        return new PagedModel<>(
                new PageImpl<>(
                        orders,
                        pageable,
                        orders.size()
                ));
    }

    @GetMapping("/done")
    public PagedModel<OrderPreviewDto> getDoneOrders(Pageable pageable) {
        Long userId = userService.getObjectByUsername(loginContext.getLoginUser().getUsername()).getId();
        List<OrderPreviewDto> orders = orderService.getDoneOrders(userId, pageable);
        return new PagedModel<>(
                new PageImpl<>(
                        orders,
                        pageable,
                        orders.size()
                ));
    }

    @GetMapping("/take")
    public ResponseEntity<?> takeWaitingOrder(Long id) {
        try {
            return ResponseEntity.ok(orderService.takeWaitingOrder(id));
        } catch (EntityNotFoundException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("/{id}")
    public OrderReadDto findOrderById(@PathVariable Long id) {
        return orderService.findOrderById(id);
    }

    @PostMapping
    public ResponseEntity<?> createOrder(@RequestParam List<Long> cartItemIds) {
        try {
            return ResponseEntity.ok(orderService.createOrder(cartItemIds));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> cancelOrder(@PathVariable Long id) {
        try {
            orderService.cancelOrder(id);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}
