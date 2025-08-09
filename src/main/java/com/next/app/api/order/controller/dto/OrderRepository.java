package com.next.app.api.order.controller.dto;

import com.next.app.api.order.entity.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface OrderRepository extends JpaRepository<Order, Long> {

    // 단건 조회: 주문 + 유저 + 아이템 + 아이템의 상품까지 한 번에 로딩 (컬렉션은 1개만 fetch join)
    @Query("""
           select distinct o
           from Order o
           left join fetch o.user
           left join fetch o.orderItems oi
           left join fetch oi.product
           where o.id = :id
           """)
    Optional<Order> findByIdWithItems(@Param("id") Long id);

    // 전체 조회: 주문 + 유저 + 아이템 + 아이템의 상품까지 한 번에 로딩
    @Query("""
           select distinct o
           from Order o
           left join fetch o.user
           left join fetch o.orderItems oi
           left join fetch oi.product
           """)
    List<Order> findAllWithItems();
}
