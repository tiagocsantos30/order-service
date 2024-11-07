package br.com.order.validator;

import br.com.order.model.Order;
import org.springframework.stereotype.Component;

@Component
public class OrderValidator {

    public void validate(Order order) {
        if (order.getItems() == null || order.getItems().isEmpty()) {
            throw new IllegalArgumentException("Order must contain at least one item.");
        }
    }
}
