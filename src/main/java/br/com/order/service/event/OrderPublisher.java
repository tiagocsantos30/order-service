package br.com.order.service.event;

import br.com.order.config.RabbitMQConfig;
import br.com.order.model.Order;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class OrderPublisher {

    private final RabbitTemplate rabbitTemplate;

    @Autowired
    public OrderPublisher(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }

    public void publishOrder(Order order) {
        rabbitTemplate.convertAndSend(RabbitMQConfig.EXCHANGE_NAME, "processed", order);

    }
}