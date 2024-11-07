package br.com.order.generator;

import br.com.order.config.RabbitMQConfig;
import br.com.order.enumerator.OrderStatus;
import br.com.order.model.Order;
import br.com.order.model.OrderItem;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.UUID;

@Service
public class GenerateOrderQueue {

    @Autowired
    private RabbitTemplate rabbitTemplate;

    public void sendOrders() {
        for (int i = 0; i < 1000; i++) {
            Order order = createOrder(i);
            rabbitTemplate.convertAndSend(RabbitMQConfig.EXCHANGE_NAME, "received", order);
            System.out.println("Order sent: " + order.getId());
        }
    }

    private Order createOrder(int index) {
        Order order = new Order();
        order.setId(UUID.randomUUID().getMostSignificantBits() & Long.MAX_VALUE);
        order.setExternalOrderId(UUID.randomUUID().toString());
        order.setOrderStatus(OrderStatus.PENDING);

        OrderItem item = new OrderItem();
        item.setId(UUID.randomUUID().getMostSignificantBits() & Long.MAX_VALUE); // ID randômico para o item
        item.setDescription("Produto " + index);
        item.setPrice(new BigDecimal("10.00"));
        item.setQuantity(2);
        order.setItems(Collections.singletonList(item));
        return order;
    }
}
