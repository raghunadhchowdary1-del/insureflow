package com.insureflow.insureflow.service;

import com.insureflow.insureflow.entity.*;
import com.insureflow.insureflow.exception.ResourceNotFoundException;
import com.insureflow.insureflow.repository.PaymentRepository;
import com.razorpay.RazorpayClient;
import com.razorpay.Utils;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

@Service
public class PaymentService {

    private final PaymentRepository paymentRepository;

    private final PolicyPurchaseService policyPurchaseService;

    public PaymentService(PaymentRepository paymentRepository, PolicyPurchaseService policyPurchaseService) {
        this.paymentRepository = paymentRepository;
        this.policyPurchaseService = policyPurchaseService;
    }

    @Value("${razorpay.key.id}")
    private String razorpayKeyId;

    @Value("${razorpay.key.secret}")
    private String razorpayKeySecret;

    public Payment createOrder(PolicyPurchase purchase) throws Exception {
        RazorpayClient client = new RazorpayClient(razorpayKeyId, razorpayKeySecret);

        double amount = purchase.getPolicyPlan().getPremiumAmount();
        int amountInPaise = (int) (amount * 100);

        JSONObject orderRequest = new JSONObject();
        orderRequest.put("amount", amountInPaise);
        orderRequest.put("currency", "INR");
        orderRequest.put("receipt", purchase.getPolicyNumber());

        com.razorpay.Order order = client.orders.create(orderRequest);

        Payment payment = new Payment();
        payment.setPolicyPurchase(purchase);
        payment.setRazorpayOrderId(order.get("id"));
        payment.setAmount(amount);
        payment.setStatus(PaymentStatus.CREATED);

        return paymentRepository.save(payment);
    }

    public String getKeyId() {
        return razorpayKeyId;
    }

    // Called by the browser's fetch() callback right after Razorpay's popup completes
    public boolean verifyAndCompletePayment(String orderId, String paymentId, String signature) throws Exception {
        Payment payment = paymentRepository.findByRazorpayOrderId(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Payment record not found for order: " + orderId));

        // Avoid double-processing if webhook already handled this before the browser callback arrived
        if (payment.getStatus() == PaymentStatus.SUCCESS) {
            return true;
        }

        JSONObject options = new JSONObject();
        options.put("razorpay_order_id", orderId);
        options.put("razorpay_payment_id", paymentId);
        options.put("razorpay_signature", signature);

        boolean isValid = Utils.verifyPaymentSignature(options, razorpayKeySecret);

        if (isValid) {
            payment.setRazorpayPaymentId(paymentId);
            payment.setRazorpaySignature(signature);
            payment.setStatus(PaymentStatus.SUCCESS);
            paymentRepository.save(payment);
            policyPurchaseService.activatePurchase(payment.getPolicyPurchase());
        } else {
            payment.setStatus(PaymentStatus.FAILED);
            paymentRepository.save(payment);
        }

        return isValid;
    }

    // Called by the webhook - the reliable source of truth, independent of the browser
    public void handleWebhookPaymentCaptured(String orderId, String paymentId) {
        paymentRepository.findByRazorpayOrderId(orderId).ifPresent(payment -> {
            if (payment.getStatus() != PaymentStatus.SUCCESS) {
                payment.setRazorpayPaymentId(paymentId);
                payment.setStatus(PaymentStatus.SUCCESS);
                paymentRepository.save(payment);
                policyPurchaseService.activatePurchase(payment.getPolicyPurchase());
                System.out.println("Webhook: Payment " + paymentId + " captured and policy activated");
            }
        });
    }

    public void handleWebhookPaymentFailed(String orderId) {
        paymentRepository.findByRazorpayOrderId(orderId).ifPresent(payment -> {
            if (payment.getStatus() != PaymentStatus.SUCCESS) {
                payment.setStatus(PaymentStatus.FAILED);
                paymentRepository.save(payment);
                System.out.println("Webhook: Payment for order " + orderId + " marked as failed");
            }
        });
    }

    // Retry: reuses the existing PENDING_PAYMENT purchase instead of creating a new one
    public Payment createRetryOrder(PolicyPurchase purchase) throws Exception {
        return createOrder(purchase);
    }

    public Page<Payment> getPaymentHistoryForUser(User user, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        return paymentRepository.findByPolicyPurchaseUser(user, pageable);
    }
}