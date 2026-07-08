package com.insureflow.insureflow.service;

import com.insureflow.insureflow.entity.PolicyPurchase;
import com.insureflow.insureflow.entity.PolicyStatus;
import com.insureflow.insureflow.repository.PolicyPurchaseRepository;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.List;

@Component
public class PolicyExpiryScheduler {

    private final PolicyPurchaseRepository policyPurchaseRepository;
    private final EmailService emailService;

    // Policies are valid for 365 days from purchase in this project's business rule
    private static final int POLICY_VALIDITY_DAYS = 365;

    public PolicyExpiryScheduler(PolicyPurchaseRepository policyPurchaseRepository, EmailService emailService) {
        this.policyPurchaseRepository = policyPurchaseRepository;
        this.emailService = emailService;
    }

    // Runs once every day at 2:00 AM server time
    @Scheduled(cron = "0 0 2 * * *")
    public void expireOldPolicies() {
        LocalDate cutoffDate = LocalDate.now().minusDays(POLICY_VALIDITY_DAYS);
        List<PolicyPurchase> expiredOnes = policyPurchaseRepository
                .findByStatusAndPurchaseDateBefore(PolicyStatus.ACTIVE, cutoffDate);

        for (PolicyPurchase purchase : expiredOnes) {
            purchase.setStatus(PolicyStatus.EXPIRED);
            policyPurchaseRepository.save(purchase);

            emailService.sendEmail(
                    purchase.getUser().getEmail(),
                    "Policy Expired - " + purchase.getPolicyNumber(),
                    "Dear " + purchase.getUser().getName() + ",\n\n" +
                            "Your policy " + purchase.getPolicyNumber() + " (" + purchase.getPolicyPlan().getName() +
                            ") has expired after 365 days.\n\n" +
                            "Please renew to continue your coverage.\n\n" +
                            "- InsureFlow Team"
            );
        }
    }
}