package br.com.order.dto;

import br.com.order.enumerator.OrderStatus;
import br.com.order.model.OrderItem;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class OrderDTO {
    private Long id;
    private List<OrderItem> items;
    private BigDecimal totalValue;
    private OrderStatus orderStatus;
    private LocalDateTime createdAt;
    private String externalOrderId;
}