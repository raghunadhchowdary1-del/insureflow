package com.insureflow.insureflow.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Entity
@Table(name = "payments")
@Data
@EqualsAndHashCode(callSuper = true)
public class Payment extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "policy_purchase_id")
    private PolicyPurchase policyPurchase;

    private String razorpayOrderId;

    private String razorpayPaymentId;

    private String razorpaySignature;

    private Double amount;

    @Enumerated(EnumType.STRING)
    private PaymentStatus status;
}