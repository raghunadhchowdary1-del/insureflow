package com.insureflow.insureflow.controller;

import com.insureflow.insureflow.dto.ClaimRequest;
import com.insureflow.insureflow.entity.PolicyPurchase;
import com.insureflow.insureflow.exception.UnauthorizedActionException;
import com.insureflow.insureflow.service.ClaimService;
import com.insureflow.insureflow.service.CustomUserDetails;
import com.insureflow.insureflow.service.PolicyPurchaseService;
import jakarta.validation.Valid;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/customer/claims")
public class ClaimController {

    private final ClaimService claimService;
    private final PolicyPurchaseService policyPurchaseService;

    public ClaimController(ClaimService claimService, PolicyPurchaseService policyPurchaseService) {
        this.claimService = claimService;
        this.policyPurchaseService = policyPurchaseService;
    }

    @GetMapping
    public String myClaims(Model model, @AuthenticationPrincipal CustomUserDetails userDetails) {
        model.addAttribute("claims", claimService.getClaimsForUser(userDetails.getUser()));
        return "customer/claims";
    }

    @GetMapping("/file/{purchaseId}")
    public String fileClaimForm(@PathVariable Long purchaseId,
                                 Model model,
                                 @AuthenticationPrincipal CustomUserDetails userDetails) {
        PolicyPurchase purchase = policyPurchaseService.getPurchaseById(purchaseId);

        if (!purchase.getUser().getId().equals(userDetails.getUser().getId())) {
            throw new UnauthorizedActionException("You do not have permission to file a claim for this policy");
        }

        model.addAttribute("purchase", purchase);
        model.addAttribute("claimRequest", new ClaimRequest());
        return "customer/file-claim";
    }

    @PostMapping("/file/{purchaseId}")
    public String submitClaim(@PathVariable Long purchaseId,
                               @Valid @ModelAttribute("claimRequest") ClaimRequest request,
                               BindingResult result,
                               Model model,
                               @AuthenticationPrincipal CustomUserDetails userDetails) {

        PolicyPurchase purchase = policyPurchaseService.getPurchaseById(purchaseId);

        if (!purchase.getUser().getId().equals(userDetails.getUser().getId())) {
            throw new UnauthorizedActionException("You do not have permission to file a claim for this policy");
        }

        if (result.hasErrors()) {
            model.addAttribute("purchase", purchase);
            return "customer/file-claim";
        }

        claimService.fileClaim(purchase, request);
        return "redirect:/customer/claims";
    }
}