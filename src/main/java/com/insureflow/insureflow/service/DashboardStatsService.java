package com.insureflow.insureflow.service;

import com.insureflow.insureflow.entity.ClaimStatus;
import com.insureflow.insureflow.entity.PolicyStatus;
import com.insureflow.insureflow.entity.PolicyType;
import com.insureflow.insureflow.repository.ClaimRepository;
import com.insureflow.insureflow.repository.PolicyPurchaseRepository;
import org.springframework.stereotype.Service;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Service
public class DashboardStatsService {

    private final PolicyPurchaseRepository policyPurchaseRepository;

    private final ClaimRepository claimRepository;

    public DashboardStatsService(PolicyPurchaseRepository policyPurchaseRepository, ClaimRepository claimRepository) {
        this.policyPurchaseRepository = policyPurchaseRepository;
        this.claimRepository = claimRepository;
    }

    public long getTotalPoliciesSold() {
        return policyPurchaseRepository.count();
    }

    public long getActivePoliciesCount() {
        return policyPurchaseRepository.countByStatus(PolicyStatus.ACTIVE);
    }

    public double getTotalPremiumCollected() {
        Double total = policyPurchaseRepository.getTotalPremiumCollected();
        return total != null ? total : 0.0;
    }

    public Map<String, Long> getClaimsByStatus() {
        Map<String, Long> map = new LinkedHashMap<>();
        for (ClaimStatus status : ClaimStatus.values()) {
            map.put(status.name(), claimRepository.countByStatus(status));
        }
        return map;
    }

    public Map<String, Long> getPoliciesByType() {
        Map<String, Long> map = new LinkedHashMap<>();
        for (PolicyType type : PolicyType.values()) {
            map.put(type.name(), 0L);
        }
        List<Object[]> results = policyPurchaseRepository.countGroupedByPlanType();
        for (Object[] row : results) {
            PolicyType type = (PolicyType) row[0];
            Long count = (Long) row[1];
            map.put(type.name(), count);
        }
        return map;
    }
}