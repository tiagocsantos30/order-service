package br.com.order.controller;

import br.com.order.dto.OrderDTO;
import br.com.order.enumerator.OrderStatus;
import br.com.order.model.Order;
import br.com.order.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/orders")
public class OrderController {

    private final OrderService orderService;

    @Autowired
    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    @GetMapping("/status/{status}")
    public List<OrderDTO> getOrdersByStatus(@PathVariable OrderStatus status,
                                            @RequestParam(defaultValue = "0") int page,
                                            @RequestParam(defaultValue = "10") int size) {
        Page<Order> ordersPage = orderService.getOrdersByStatus(status, page, size);
        return ordersPage.stream()
                .map(order -> new OrderDTO(
                        order.getId(),
                        order.getItems(),
                        order.getTotalValue(),
                        order.getOrderStatus(),
                        order.getCreatedAt(),
                        order.getExternalOrderId()
                ))
                .collect(Collectors.toList());
    }
}