package com.insureflow.insureflow.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDate;

@Entity
@Table(name = "claims")
@Data
@EqualsAndHashCode(callSuper = true)
public class Claim extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "policy_purchase_id")
    private PolicyPurchase policyPurchase;

    @Positive(message = "Claim amount must be positive")
    private Double claimAmount;

    @NotBlank(message = "Reason is required")
    private String reason;

    @Enumerated(EnumType.STRING)
    private ClaimStatus status;

    private LocalDate filedDate;

    private String remarks;
}