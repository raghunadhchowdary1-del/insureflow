package com.insureflow.insureflow.controller;

import com.insureflow.insureflow.dto.PolicyPlanRequest;
import com.insureflow.insureflow.entity.PolicyPlan;
import com.insureflow.insureflow.entity.PolicyType;
import com.insureflow.insureflow.service.PolicyPlanService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class AdminPolicyController {

    private final PolicyPlanService policyPlanService;

    public AdminPolicyController(PolicyPlanService policyPlanService) {
        this.policyPlanService = policyPlanService;
    }

    @GetMapping("/admin/policies")
    public String listPolicies(@RequestParam(defaultValue = "0") int page, Model model) {
        Page<PolicyPlan> plansPage = policyPlanService.getAllPlansPaged(page, 5);
        model.addAttribute("plansPage", plansPage);
        model.addAttribute("policyPlanRequest", new PolicyPlanRequest());
        model.addAttribute("types", PolicyType.values());
        return "admin/policies";
    }

    @PostMapping("/admin/policies")
    public String createPolicy(@Valid @ModelAttribute("policyPlanRequest") PolicyPlanRequest request,
                                BindingResult result,
                                Model model) {
        if (result.hasErrors()) {
            model.addAttribute("plansPage", policyPlanService.getAllPlansPaged(0, 5));
            model.addAttribute("types", PolicyType.values());
            return "admin/policies";
        }
        policyPlanService.createPlan(request);
        return "redirect:/admin/policies";
    }
}