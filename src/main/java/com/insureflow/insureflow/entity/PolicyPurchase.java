package com.insureflow.insureflow.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDate;

@Entity
@Table(name = "policy_purchases")
@Data
@EqualsAndHashCode(callSuper = true)
public class PolicyPurchase extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true)
    private String policyNumber;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne
    @JoinColumn(name = "policy_plan_id")
    private PolicyPlan policyPlan;

    private LocalDate purchaseDate;

    @Enumerated(EnumType.STRING)
    private PolicyStatus status;
}