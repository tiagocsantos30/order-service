package br.com.order.repository;

import br.com.order.model.Order;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface OrderRepository extends JpaRepository<Order, Long> {
    Optional<Order> findByExternalOrderId(String externalOrderId);
}
