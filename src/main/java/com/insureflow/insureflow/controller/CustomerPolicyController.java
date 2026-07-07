package com.insureflow.insureflow.controller;

import com.insureflow.insureflow.service.CustomUserDetails;
import com.insureflow.insureflow.service.PolicyPlanService;
import com.insureflow.insureflow.service.PolicyPurchaseService;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
@org.springframework.web.bind.annotation.RequestMapping("/customer")
public class CustomerPolicyController {

    private PolicyPlanService policyPlanService;
    private PolicyPurchaseService policyPurchaseService;

    public CustomerPolicyController(PolicyPlanService policyPlanService, PolicyPurchaseService policyPurchaseService) {
        this.policyPlanService = policyPlanService;
        this.policyPurchaseService = policyPurchaseService;
    }

    @GetMapping("/policies")
    public String availablePolicies(Model model) {
        model.addAttribute("plans", policyPlanService.getAllPlans());
        return "customer/policies";
    }

    @PostMapping("/policies/buy/{planId}")
    public String buyPolicy(@PathVariable Long planId,
                             @AuthenticationPrincipal CustomUserDetails userDetails) {
        policyPurchaseService.purchasePlan(userDetails.getUser(), planId);
        return "redirect:/customer/my-policies";
    }

    @GetMapping("/my-policies")
    public String myPolicies(Model model, @AuthenticationPrincipal CustomUserDetails userDetails) {
        model.addAttribute("purchases", policyPurchaseService.getPurchasesForUser(userDetails.getUser()));
        return "customer/my-policies";
    }
}