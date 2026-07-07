package com.insureflow.insureflow.service;

import com.insureflow.insureflow.dto.PolicyPlanRequest;
import com.insureflow.insureflow.entity.PolicyPlan;
import com.insureflow.insureflow.repository.PolicyPlanRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PolicyPlanService {

    private final PolicyPlanRepository policyPlanRepository;

    public PolicyPlanService(PolicyPlanRepository policyPlanRepository) {
        this.policyPlanRepository = policyPlanRepository;
    }

    public List<PolicyPlan> getAllPlans() {
        return policyPlanRepository.findAll();
    }

    public PolicyPlan getPlanById(Long id) {
        return policyPlanRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Policy plan not found with id: " + id));
    }

    public PolicyPlan createPlan(PolicyPlanRequest request) {
        PolicyPlan plan = new PolicyPlan();
        plan.setName(request.getName());
        plan.setType(request.getType());
        plan.setDescription(request.getDescription());
        plan.setPremiumAmount(request.getPremiumAmount());
        plan.setCoverageAmount(request.getCoverageAmount());
        return policyPlanRepository.save(plan);
    }
}