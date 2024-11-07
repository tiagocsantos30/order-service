package br.com.order.service;

import br.com.order.calculator.OrderCalculator;
import br.com.order.enumerator.OrderStatus;
import br.com.order.exception.DuplicateOrderException;
import br.com.order.model.Order;
import br.com.order.repository.OrderRepository;
import br.com.order.service.event.OrderPublisher;
import br.com.order.validator.OrderValidator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Collections;

@Service
public class OrderService {

    private static final Logger logger = LoggerFactory.getLogger(OrderService.class);

    private final OrderRepository orderRepository;
    private final OrderPublisher publish;
    private final OrderValidator orderValidator;
    private final OrderCalculator orderCalculator;

    public OrderService(OrderRepository orderRepository, OrderPublisher publish, OrderValidator orderValidator, OrderCalculator orderCalculator) {
        this.orderRepository = orderRepository;
        this.publish = publish;
        this.orderValidator = orderValidator;
        this.orderCalculator = orderCalculator;
    }

    @Transactional
    public Order processOrder(Order order) throws DuplicateOrderException, IllegalArgumentException {
        logger.info("Processing order: {}", order.getExternalOrderId());

        orderRepository.findByExternalOrderId(order.getExternalOrderId())
                .ifPresent(o -> {
                    logger.error("Duplicate order found: {}", o.getExternalOrderId());
                    throw new DuplicateOrderException("Order already exists: " + o.getExternalOrderId());
                });

        orderValidator.validate(order);

        order.setTotalValue(orderCalculator.calculateTotal(order));
        order.setOrderStatus(OrderStatus.PROCESSED);
        order.setCreatedAt(LocalDateTime.now());

        if (order.getItems() == null) {
            order.setItems(Collections.emptyList());
        }

        order.getItems().forEach(item -> item.setOrder(order));
        Order savedOrder = orderRepository.save(order);

        publish.publishOrder(savedOrder);

        logger.info("Order processed successfully: {}", savedOrder.getExternalOrderId());
        return savedOrder;
    }
}