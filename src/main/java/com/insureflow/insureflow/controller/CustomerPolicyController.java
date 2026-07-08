package com.insureflow.insureflow.controller;

import com.insureflow.insureflow.service.AppUserPrincipal;
import com.insureflow.insureflow.service.PolicyPlanService;
import com.insureflow.insureflow.service.PolicyPurchaseService;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@org.springframework.web.bind.annotation.RequestMapping("/customer")
public class CustomerPolicyController {

    private final PolicyPlanService policyPlanService;
    private final PolicyPurchaseService policyPurchaseService;

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
                             @AuthenticationPrincipal AppUserPrincipal userDetails) {
        policyPurchaseService.purchasePlan(userDetails.getUser(), planId);
        return "redirect:/customer/my-policies";
    }

    @GetMapping("/my-policies")
    public String myPolicies(@RequestParam(defaultValue = "0") int page,
                              Model model,
                              @AuthenticationPrincipal AppUserPrincipal userDetails) {
        model.addAttribute("purchasesPage", policyPurchaseService.getPurchasesForUser(userDetails.getUser(), page, 5));
        return "customer/my-policies";
    }
}