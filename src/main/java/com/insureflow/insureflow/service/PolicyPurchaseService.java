package com.insureflow.insureflow.service;

import com.insureflow.insureflow.entity.*;
import com.insureflow.insureflow.exception.ResourceNotFoundException;
import com.insureflow.insureflow.repository.PolicyPurchaseRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Service
public class PolicyPurchaseService {

    private final PolicyPurchaseRepository policyPurchaseRepository;
    private final PolicyPlanService policyPlanService;

    public PolicyPurchaseService(PolicyPurchaseRepository policyPurchaseRepository,
                                 PolicyPlanService policyPlanService) {
        this.policyPurchaseRepository = policyPurchaseRepository;
        this.policyPlanService = policyPlanService;
    }

    public PolicyPurchase purchasePlan(User user, Long planId) {
        PolicyPlan plan = policyPlanService.getPlanById(planId);

        PolicyPurchase purchase = new PolicyPurchase();
        purchase.setUser(user);
        purchase.setPolicyPlan(plan);
        purchase.setPurchaseDate(LocalDate.now());
        purchase.setStatus(PolicyStatus.ACTIVE);
        purchase.setPolicyNumber("POL-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase());

        return policyPurchaseRepository.save(purchase);
    }

    public List<PolicyPurchase> getPurchasesForUser(User user) {
        return policyPurchaseRepository.findByUser(user);
    }

    public PolicyPurchase getPurchaseById(Long id) {
        return policyPurchaseRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Policy purchase not found with id: " + id));
    }
}