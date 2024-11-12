package br.com.order.service;

import br.com.order.calculator.OrderCalculator;
import br.com.order.enumerator.OrderStatus;
import br.com.order.exception.DuplicateOrderException;
import br.com.order.model.Order;
import br.com.order.repository.OrderRepository;
import br.com.order.service.event.OrderPublisher;
import br.com.order.validator.OrderValidator;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class OrderServiceTest {

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private OrderPublisher orderPublisher;

    @Mock
    private OrderValidator orderValidator;

    @Mock
    private OrderCalculator orderCalculator;

    @InjectMocks
    private OrderService orderService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void processOrder_success() throws DuplicateOrderException {
        Order order = new Order();
        order.setExternalOrderId("123");
        when(orderRepository.findByExternalOrderId(any())).thenReturn(Optional.empty());
        doNothing().when(orderValidator).validate(any());
        when(orderCalculator.calculateTotal(any())).thenReturn(new BigDecimal(100));
        when(orderRepository.save(any())).thenReturn(order);

        Order processedOrder = orderService.processOrder(order);

        assertNotNull(processedOrder);
        assertEquals(OrderStatus.PROCESSED, processedOrder.getOrderStatus());
        verify(orderPublisher, times(1)).publishOrder(processedOrder);
    }

    @Test
    void processOrder_duplicateOrderException() {
        Order order = new Order();
        order.setExternalOrderId("123");
        when(orderRepository.findByExternalOrderId(any())).thenReturn(Optional.of(order));

        assertThrows(DuplicateOrderException.class, () -> orderService.processOrder(order));
    }

    @Test
    void processOrder_validationFailure() {
        Order order = new Order();
        order.setExternalOrderId("123");
        when(orderRepository.findByExternalOrderId(any())).thenReturn(Optional.empty());
        doThrow(new IllegalArgumentException("Invalid order")).when(orderValidator).validate(any());

        assertThrows(IllegalArgumentException.class, () -> orderService.processOrder(order));
    }

    @Test
    void getOrdersByStatus_ordersFound() {
        Order order = new Order();
        order.setId(1L);
        order.setOrderStatus(OrderStatus.PROCESSED);
        Page<Order> ordersPage = new PageImpl<>(Collections.singletonList(order));
        Pageable pageable = PageRequest.of(0, 10);

        when(orderRepository.findByOrderStatus(OrderStatus.PROCESSED, pageable)).thenReturn(ordersPage);

        Page<Order> result = orderService.getOrdersByStatus(OrderStatus.PROCESSED, 0, 10);

        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
        assertEquals(OrderStatus.PROCESSED, result.getContent().get(0).getOrderStatus());
    }

    @Test
    void getOrdersByStatus_noOrdersFound() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<Order> emptyPage = new PageImpl<>(Collections.emptyList());

        when(orderRepository.findByOrderStatus(OrderStatus.PROCESSED, pageable)).thenReturn(emptyPage);

        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class, () -> {
            orderService.getOrdersByStatus(OrderStatus.PROCESSED, 0, 10);
        });

        assertEquals("No orders found with status: PROCESSED", exception.getMessage());
    }
}