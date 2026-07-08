package com.insureflow.insureflow.repository;

import com.insureflow.insureflow.entity.PolicyPurchase;
import com.insureflow.insureflow.entity.PolicyStatus;
import com.insureflow.insureflow.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDate;
import java.util.List;

public interface PolicyPurchaseRepository extends JpaRepository<PolicyPurchase, Long> {
    List<PolicyPurchase> findByUser(User user);
    Page<PolicyPurchase> findByUser(User user, Pageable pageable);
    List<PolicyPurchase> findByStatusAndPurchaseDateBefore(PolicyStatus status, LocalDate date);

    long countByStatus(PolicyStatus status);

    @Query("SELECT COALESCE(SUM(p.policyPlan.premiumAmount), 0) FROM PolicyPurchase p")
    Double getTotalPremiumCollected();

    @Query("SELECT p.policyPlan.type, COUNT(p) FROM PolicyPurchase p GROUP BY p.policyPlan.type")
    List<Object[]> countGroupedByPlanType();
}