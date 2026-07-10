package com.insureflow.insureflow.repository;

import com.insureflow.insureflow.entity.Payment;
import com.insureflow.insureflow.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PaymentRepository extends JpaRepository<Payment, Long> {
    Optional<Payment> findByRazorpayOrderId(String orderId);
    Page<Payment> findByPolicyPurchaseUser(User user, Pageable pageable);
}