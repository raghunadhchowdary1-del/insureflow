package com.insureflow.insureflow.service;

import com.insureflow.insureflow.entity.*;
import com.insureflow.insureflow.exception.ResourceNotFoundException;
import com.insureflow.insureflow.repository.PolicyPurchaseRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.UUID;

@Service
public class PolicyPurchaseService {

    private final PolicyPurchaseRepository policyPurchaseRepository;

    private final PolicyPlanService policyPlanService;

    public PolicyPurchaseService(PolicyPurchaseRepository policyPurchaseRepository, PolicyPlanService policyPlanService) {
        this.policyPurchaseRepository = policyPurchaseRepository;
        this.policyPlanService = policyPlanService;
    }

    // Creates the purchase record in PENDING_PAYMENT state - it only becomes ACTIVE after successful payment
    public PolicyPurchase initiatePurchase(User user, Long planId) {
        PolicyPlan plan = policyPlanService.getPlanById(planId);

        PolicyPurchase purchase = new PolicyPurchase();
        purchase.setUser(user);
        purchase.setPolicyPlan(plan);
        purchase.setPurchaseDate(LocalDate.now());
        purchase.setStatus(PolicyStatus.PENDING_PAYMENT);
        purchase.setPolicyNumber("POL-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase());

        return policyPurchaseRepository.save(purchase);
    }

    public void activatePurchase(PolicyPurchase purchase) {
        purchase.setStatus(PolicyStatus.ACTIVE);
        policyPurchaseRepository.save(purchase);
    }

    public Page<PolicyPurchase> getPurchasesForUser(User user, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("purchaseDate").descending());
        return policyPurchaseRepository.findByUser(user, pageable);
    }

    public PolicyPurchase getPurchaseById(Long id) {
        return policyPurchaseRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Policy purchase not found with id: " + id));
    }
}