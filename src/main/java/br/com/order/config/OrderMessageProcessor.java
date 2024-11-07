package br.com.order.config;

import br.com.order.model.Order;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class OrderMessageProcessor {

    @Autowired
    private ObjectMapper objectMapper;

    private static final Logger logger = LoggerFactory.getLogger(OrderMessageProcessor.class);


    public void handleMessage(byte[] message) {
        try {
            String messageStr = new String(message);
            Order order = objectMapper.readValue(messageStr, Order.class);
            System.out.println("Message received: " + order);
        } catch (Exception e) {
            logger.error("Error processing message", e);
        }
    }
}