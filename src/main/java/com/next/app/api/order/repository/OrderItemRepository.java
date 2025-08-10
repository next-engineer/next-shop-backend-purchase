package com.next.app.api.order.repository;

import com.next.app.api.order.entity.OrderItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface OrderItemRepository extends JpaRepository<OrderItem, Long> {

    @Modifying
    @Query("delete from OrderItem oi where oi.order.id = :orderId")
    void deleteByOrderId(@Param("orderId") Long orderId);
}
