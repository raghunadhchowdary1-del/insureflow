package com.insureflow.insureflow.controller;

import com.insureflow.insureflow.service.ClaimService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/admin/claims")
public class AdminClaimController {

    private final ClaimService claimService;

    public AdminClaimController(ClaimService claimService) {
        this.claimService = claimService;
    }

    @GetMapping
    public String viewClaims(Model model) {
        model.addAttribute("claims", claimService.getAllClaims());
        return "admin/claims";
    }

    @PostMapping("/approve/{id}")
    public String approve(@PathVariable Long id, @RequestParam(required = false) String remarks) {
        claimService.approveClaim(id, remarks != null ? remarks : "Approved");
        return "redirect:/admin/claims";
    }

    @PostMapping("/reject/{id}")
    public String reject(@PathVariable Long id, @RequestParam(required = false) String remarks) {
        claimService.rejectClaim(id, remarks != null ? remarks : "Rejected");
        return "redirect:/admin/claims";
    }
}