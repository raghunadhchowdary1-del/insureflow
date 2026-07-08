package com.insureflow.insureflow.repository;

import com.insureflow.insureflow.entity.PolicyPurchase;
import com.insureflow.insureflow.entity.PolicyStatus;
import com.insureflow.insureflow.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;

public interface PolicyPurchaseRepository extends JpaRepository<PolicyPurchase, Long> {
    List<PolicyPurchase> findByUser(User user);
    List<PolicyPurchase> findByStatusAndPurchaseDateBefore(PolicyStatus status, LocalDate date);
}