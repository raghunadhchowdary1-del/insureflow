package com.insureflow.insureflow.controller;

import com.insureflow.insureflow.dto.PaymentVerificationRequest;
import com.insureflow.insureflow.entity.Payment;
import com.insureflow.insureflow.entity.PolicyPurchase;
import com.insureflow.insureflow.service.AppUserPrincipal;
import com.insureflow.insureflow.service.PaymentService;
import com.insureflow.insureflow.service.PolicyPurchaseService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/customer/payment")
public class PaymentController {

    private final PolicyPurchaseService policyPurchaseService;
    private final PaymentService paymentService;

    public PaymentController(PolicyPurchaseService policyPurchaseService,
                             PaymentService paymentService) {
        this.policyPurchaseService = policyPurchaseService;
        this.paymentService = paymentService;
    }

    @PostMapping("/initiate/{planId}")
    public String initiatePayment(@PathVariable Long planId,
                                   @AuthenticationPrincipal AppUserPrincipal userDetails,
                                   Model model) throws Exception {

        PolicyPurchase purchase = policyPurchaseService.initiatePurchase(userDetails.getUser(), planId);
        Payment payment = paymentService.createOrder(purchase);

        model.addAttribute("razorpayKeyId", paymentService.getKeyId());
        model.addAttribute("orderId", payment.getRazorpayOrderId());
        model.addAttribute("amount", (int) (payment.getAmount() * 100));
        model.addAttribute("purchase", purchase);
        model.addAttribute("customerName", userDetails.getUser().getName());
        model.addAttribute("customerEmail", userDetails.getUser().getEmail());

        return "customer/checkout";
    }

    @PostMapping("/verify")
    @ResponseBody
    public ResponseEntity<String> verifyPayment(@RequestBody PaymentVerificationRequest request) {
        try {
            boolean valid = paymentService.verifyAndCompletePayment(
                    request.getRazorpayOrderId(),
                    request.getRazorpayPaymentId(),
                    request.getRazorpaySignature()
            );
            return valid ? ResponseEntity.ok("SUCCESS") : ResponseEntity.badRequest().body("INVALID_SIGNATURE");
        } catch (Exception e) {
            System.err.println("=== PAYMENT VERIFICATION ERROR ===");
            e.printStackTrace();
            System.err.println("==================================");
            return ResponseEntity.internalServerError().body("ERROR: " + e.getMessage());
        }
    }
}