package com.insureflow.insureflow.dto;

import com.insureflow.insureflow.entity.PolicyType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;

@Data
public class PolicyPlanRequest {

    @NotBlank(message = "Plan name is required")
    private String name;

    @NotNull(message = "Type is required")
    private PolicyType type;

    private String description;

    @NotNull(message = "Premium is required")
    @Positive(message = "Premium must be positive")
    private Double premiumAmount;

    @NotNull(message = "Coverage is required")
    @Positive(message = "Coverage must be positive")
    private Double coverageAmount;
}