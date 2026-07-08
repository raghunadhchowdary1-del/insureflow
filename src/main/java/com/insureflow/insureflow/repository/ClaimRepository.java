package com.insureflow.insureflow.repository;

import com.insureflow.insureflow.entity.Claim;
import com.insureflow.insureflow.entity.ClaimStatus;
import com.insureflow.insureflow.entity.PolicyPurchase;
import com.insureflow.insureflow.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ClaimRepository extends JpaRepository<Claim, Long> {
    List<Claim> findByPolicyPurchase(PolicyPurchase policyPurchase);
    List<Claim> findByPolicyPurchaseUser(User user);

    Page<Claim> findByPolicyPurchaseUser(User user, Pageable pageable);

    @Query("SELECT c FROM Claim c WHERE " +
           "(:status IS NULL OR c.status = :status) AND " +
           "(:customerName IS NULL OR LOWER(c.policyPurchase.user.name) LIKE LOWER(CONCAT('%', :customerName, '%')))")
    Page<Claim> findWithFilters(@Param("status") ClaimStatus status,
                                 @Param("customerName") String customerName,
                                 Pageable pageable);

    long countByStatus(ClaimStatus status);
}