package com.insureflow.insureflow.controller;

import com.insureflow.insureflow.service.ClaimService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
@org.springframework.web.bind.annotation.RequestMapping("/agent/claims")
public class AgentClaimController {

    private final ClaimService claimService;

    public AgentClaimController(ClaimService claimService) {
        this.claimService = claimService;
    }

    @GetMapping
    public String viewClaims(Model model) {
        model.addAttribute("claims", claimService.getAllClaims());
        return "agent/claims";
    }

    @PostMapping("/review/{id}")
    public String markUnderReview(@PathVariable Long id) {
        claimService.markUnderReview(id);
        return "redirect:/agent/claims";
    }
}