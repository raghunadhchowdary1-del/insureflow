package com.insureflow.insureflow.service;

import com.insureflow.insureflow.entity.*;
import com.insureflow.insureflow.exception.ResourceNotFoundException;
import com.insureflow.insureflow.repository.PaymentRepository;
import com.razorpay.RazorpayClient;
import com.razorpay.Utils;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
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

    // Creates a Razorpay order for a given policy purchase and stores a matching Payment record
    public Payment createOrder(PolicyPurchase purchase) throws Exception {
        RazorpayClient client = new RazorpayClient(razorpayKeyId, razorpayKeySecret);

        double amount = purchase.getPolicyPlan().getPremiumAmount();
        int amountInPaise = (int) (amount * 100); // Razorpay expects amount in paise

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

    // Verifies the payment signature Razorpay sends back after checkout completes
    public boolean verifyAndCompletePayment(String orderId, String paymentId, String signature) throws Exception {
        Payment payment = paymentRepository.findByRazorpayOrderId(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Payment record not found for order: " + orderId));

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
}