package com.insureflow.insureflow.service;

import com.insureflow.insureflow.dto.PolicyPlanRequest;
import com.insureflow.insureflow.entity.PolicyPlan;
import com.insureflow.insureflow.exception.ResourceNotFoundException;
import com.insureflow.insureflow.repository.PolicyPlanRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PolicyPlanService {

    private final PolicyPlanRepository policyPlanRepository;

    public PolicyPlanService(PolicyPlanRepository policyPlanRepository) {
        this.policyPlanRepository = policyPlanRepository;
    }

    // Used by customers browsing all available plans (kept unpaginated - list is small enough to browse freely)
    public List<PolicyPlan> getAllPlans() {
        return policyPlanRepository.findAll();
    }

    // Used by admin's plan management table
    public Page<PolicyPlan> getAllPlansPaged(int page, int size) {
        return policyPlanRepository.findAll(PageRequest.of(page, size, Sort.by("id").descending()));
    }

    public PolicyPlan getPlanById(Long id) {
        return policyPlanRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Policy plan not found with id: " + id));
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