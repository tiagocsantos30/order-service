package br.com.order.service;

import br.com.order.calculator.OrderCalculator;
import br.com.order.enumerator.OrderStatus;
import br.com.order.exception.DuplicateOrderException;
import br.com.order.model.Order;
import br.com.order.repository.OrderRepository;
import br.com.order.service.event.OrderPublisher;
import br.com.order.validator.OrderValidator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.math.BigDecimal;
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
}