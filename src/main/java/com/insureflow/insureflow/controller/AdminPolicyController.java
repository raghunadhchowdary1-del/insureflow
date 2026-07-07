package com.insureflow.insureflow.controller;

import com.insureflow.insureflow.dto.PolicyPlanRequest;
import com.insureflow.insureflow.entity.PolicyType;
import com.insureflow.insureflow.service.PolicyPlanService;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
public class AdminPolicyController {

    private final PolicyPlanService policyPlanService;

    public AdminPolicyController(PolicyPlanService policyPlanService) {
        this.policyPlanService = policyPlanService;
    }

    @GetMapping("/admin/policies")
    public String listPolicies(Model model) {
        model.addAttribute("plans", policyPlanService.getAllPlans());
        model.addAttribute("policyPlanRequest", new PolicyPlanRequest());
        model.addAttribute("types", PolicyType.values());
        return "admin/policies";
    }

    @PostMapping("/admin/policies")
    public String createPolicy(@Valid @ModelAttribute("policyPlanRequest") PolicyPlanRequest request,
                                BindingResult result,
                                Model model) {
        if (result.hasErrors()) {
            model.addAttribute("plans", policyPlanService.getAllPlans());
            model.addAttribute("types", PolicyType.values());
            return "admin/policies";
        }
        policyPlanService.createPlan(request);
        return "redirect:/admin/policies";
    }
}