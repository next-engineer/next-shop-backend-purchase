package com.next.app.api.order.repository;

import com.next.app.api.order.entity.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface OrderRepository extends JpaRepository<Order, Long> {

    @Query("""
           select distinct o
           from Order o
           left join fetch o.orderItems oi
           left join fetch o.user u
           """)
    List<Order> findAllWithItems();

    @Query("""
           select o
           from Order o
           left join fetch o.orderItems oi
           left join fetch o.user u
           where o.id = :id
           """)
    Optional<Order> findByIdWithItems(@Param("id") Long id);
}