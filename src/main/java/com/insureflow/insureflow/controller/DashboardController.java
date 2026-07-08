package com.insureflow.insureflow.controller;

import com.insureflow.insureflow.service.DashboardStatsService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class DashboardController {

    private final DashboardStatsService dashboardStatsService;

    public DashboardController(DashboardStatsService dashboardStatsService) {
        this.dashboardStatsService = dashboardStatsService;
    }

    @GetMapping("/customer/dashboard")
    public String customerDashboard() {
        return "customer/dashboard";
    }

    @GetMapping("/agent/dashboard")
    public String agentDashboard() {
        return "agent/dashboard";
    }

    @GetMapping("/admin/dashboard")
    public String adminDashboard(Model model) {
        model.addAttribute("totalPolicies", dashboardStatsService.getTotalPoliciesSold());
        model.addAttribute("activePolicies", dashboardStatsService.getActivePoliciesCount());
        model.addAttribute("totalPremium", dashboardStatsService.getTotalPremiumCollected());
        model.addAttribute("claimsByStatus", dashboardStatsService.getClaimsByStatus());
        model.addAttribute("policiesByType", dashboardStatsService.getPoliciesByType());
        return "admin/dashboard";
    }
}