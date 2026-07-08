package com.insureflow.insureflow.controller;

import com.insureflow.insureflow.entity.Claim;
import com.insureflow.insureflow.entity.ClaimStatus;
import com.insureflow.insureflow.service.ClaimService;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.ui.Model;

@Controller
@RequestMapping("/agent/claims")
public class AgentClaimController {

    private final ClaimService claimService;

    public AgentClaimController(ClaimService claimService) {
        this.claimService = claimService;
    }

    @GetMapping
    public String viewClaims(@RequestParam(required = false) ClaimStatus status,
                              @RequestParam(defaultValue = "0") int page,
                              Model model) {
        Page<Claim> claimsPage = claimService.getAllClaimsFiltered(status, null, page, 10);
        model.addAttribute("claimsPage", claimsPage);
        model.addAttribute("selectedStatus", status);
        model.addAttribute("statuses", ClaimStatus.values());
        return "agent/claims";
    }

    @PostMapping("/review/{id}")
    public String markUnderReview(@PathVariable Long id) {
        claimService.markUnderReview(id);
        return "redirect:/agent/claims";
    }
}