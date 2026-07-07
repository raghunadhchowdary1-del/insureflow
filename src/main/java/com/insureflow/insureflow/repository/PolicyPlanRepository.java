package com.insureflow.insureflow.repository;

import com.insureflow.insureflow.entity.PolicyPlan;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PolicyPlanRepository extends JpaRepository<PolicyPlan, Long> {
}