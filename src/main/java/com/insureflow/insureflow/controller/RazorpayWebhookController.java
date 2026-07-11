package com.insureflow.insureflow.controller;

import com.insureflow.insureflow.service.PaymentService;
import com.razorpay.Utils;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class RazorpayWebhookController {

    private final PaymentService paymentService;

    @Value("${razorpay.webhook.secret}")
    private String webhookSecret;

    public RazorpayWebhookController(PaymentService paymentService) {
        this.paymentService = paymentService;
    }

    @PostMapping("/webhook/razorpay")
    public ResponseEntity<String> handleWebhook(@RequestBody String payload,
                                                 @RequestHeader("X-Razorpay-Signature") String signature) {
        try {
            boolean isValid = Utils.verifyWebhookSignature(payload, signature, webhookSecret);

            if (!isValid) {
                System.err.println("Webhook signature verification failed - ignoring request");
                return ResponseEntity.status(400).body("Invalid signature");
            }

            JSONObject json = new JSONObject(payload);
            String event = json.getString("event");

            System.out.println("=== Razorpay Webhook Received: " + event + " ===");

            if (event.equals("payment.captured")) {
                JSONObject paymentEntity = json.getJSONObject("payload").getJSONObject("payment").getJSONObject("entity");
                String orderId = paymentEntity.getString("order_id");
                String paymentId = paymentEntity.getString("id");
                paymentService.handleWebhookPaymentCaptured(orderId, paymentId);
            } else if (event.equals("payment.failed")) {
                JSONObject paymentEntity = json.getJSONObject("payload").getJSONObject("payment").getJSONObject("entity");
                String orderId = paymentEntity.getString("order_id");
                paymentService.handleWebhookPaymentFailed(orderId);
            }

            return ResponseEntity.ok("Webhook processed");
        } catch (Exception e) {
            System.err.println("=== WEBHOOK ERROR ===");
            e.printStackTrace();
            return ResponseEntity.status(500).body("Error processing webhook");
        }
    }
}