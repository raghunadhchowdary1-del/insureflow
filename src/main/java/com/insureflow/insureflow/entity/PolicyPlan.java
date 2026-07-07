package com.insureflow.insureflow.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import lombok.Data;

@Entity
@Table(name = "policy_plans")
@Data
public class PolicyPlan {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Plan name is required")
    private String name;

    @Enumerated(EnumType.STRING)
    private PolicyType type;

    private String description;

    @Positive(message = "Premium must be positive")
    private Double premiumAmount;

    @Positive(message = "Coverage must be positive")
    private Double coverageAmount;
}