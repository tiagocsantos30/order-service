package br.com.order.service.event;

import br.com.order.config.RabbitMQConfig;
import br.com.order.model.Order;
import br.com.order.service.OrderService;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class OrderConsumer {

    private final OrderService orderService;

    @Autowired
    public OrderConsumer(OrderService orderService) {
        this.orderService = orderService;
    }

    @RabbitListener(queues = RabbitMQConfig.QUEUE_RECEIVED)
    public void handleReceivedOrder(Order order) {
        orderService.processOrder(order);
    }
}