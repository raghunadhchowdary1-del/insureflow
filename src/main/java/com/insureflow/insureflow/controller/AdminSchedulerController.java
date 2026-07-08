package com.insureflow.insureflow.controller;

import com.insureflow.insureflow.service.PolicyExpiryScheduler;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
public class AdminSchedulerController {

    private final PolicyExpiryScheduler policyExpiryScheduler;

    public AdminSchedulerController(PolicyExpiryScheduler policyExpiryScheduler) {
        this.policyExpiryScheduler = policyExpiryScheduler;
    }

    @PostMapping("/admin/run-expiry-check")
    public String runExpiryCheck() {
        policyExpiryScheduler.expireOldPolicies();
        return "redirect:/admin/dashboard?expiryChecked";
    }
}