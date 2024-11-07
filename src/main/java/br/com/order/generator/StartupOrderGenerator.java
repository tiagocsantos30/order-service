package br.com.order.generator;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
public class StartupOrderGenerator {

    @Autowired
    private GenerateOrderQueue generateOrderQueue;

    @EventListener(ContextRefreshedEvent.class)
    public void onApplicationEvent() {
        generateOrderQueue.sendOrders();
    }
}