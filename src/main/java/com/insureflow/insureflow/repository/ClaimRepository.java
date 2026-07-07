package com.insureflow.insureflow.repository;

import com.insureflow.insureflow.entity.Claim;
import com.insureflow.insureflow.entity.PolicyPurchase;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ClaimRepository extends JpaRepository<Claim, Long> {
    List<Claim> findByPolicyPurchase(PolicyPurchase policyPurchase);
    List<Claim> findByPolicyPurchaseUser(com.insureflow.insureflow.entity.User user);
}