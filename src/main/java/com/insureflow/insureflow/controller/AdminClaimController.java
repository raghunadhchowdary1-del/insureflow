package com.insureflow.insureflow.controller;

import com.insureflow.insureflow.entity.Claim;
import com.insureflow.insureflow.entity.ClaimStatus;
import com.insureflow.insureflow.service.ClaimService;
import org.springframework.data.domain.Page;
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
    public String viewClaims(@RequestParam(required = false) ClaimStatus status,
                              @RequestParam(required = false) String customerName,
                              @RequestParam(defaultValue = "0") int page,
                              Model model) {
        Page<Claim> claimsPage = claimService.getAllClaimsFiltered(status, customerName, page, 10);
        model.addAttribute("claimsPage", claimsPage);
        model.addAttribute("selectedStatus", status);
        model.addAttribute("customerName", customerName);
        model.addAttribute("statuses", ClaimStatus.values());
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