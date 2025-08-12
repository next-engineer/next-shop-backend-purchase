package com.next.app.api.payment.repository;

import com.next.app.api.payment.entity.Payment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface PaymentRepository extends JpaRepository<Payment, Long> {
    Optional<Payment> findTopByOrder_IdOrderByPaidAtDesc(Long orderId);
    List<Payment> findByOrder_Id(Long orderId);
}
