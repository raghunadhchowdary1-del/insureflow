package com.insureflow.insureflow.service;

import com.insureflow.insureflow.dto.ClaimRequest;
import com.insureflow.insureflow.entity.*;
import com.insureflow.insureflow.exception.ResourceNotFoundException;
import com.insureflow.insureflow.repository.ClaimRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.time.LocalDate;

@Service
public class ClaimService {

    private final ClaimRepository claimRepository;
    private final EmailService emailService;

    public ClaimService(ClaimRepository claimRepository, EmailService emailService) {
        this.claimRepository = claimRepository;
        this.emailService = emailService;
    }

    public Claim fileClaim(PolicyPurchase purchase, ClaimRequest request) {
        Claim claim = new Claim();
        claim.setPolicyPurchase(purchase);
        claim.setClaimAmount(request.getClaimAmount());
        claim.setReason(request.getReason());
        claim.setStatus(ClaimStatus.PENDING);
        claim.setFiledDate(LocalDate.now());
        Claim saved = claimRepository.save(claim);

        emailService.sendEmail(
                purchase.getUser().getEmail(),
                "Claim Filed Successfully - " + purchase.getPolicyNumber(),
                "Dear " + purchase.getUser().getName() + ",\n\n" +
                        "Your claim of ₹" + request.getClaimAmount() + " for policy " + purchase.getPolicyNumber() +
                        " has been filed and is now PENDING review.\n\n" +
                        "Reason: " + request.getReason() + "\n\n" +
                        "We will notify you once it is reviewed.\n\n" +
                        "- InsureFlow Team"
        );

        return saved;
    }

    public Page<Claim> getClaimsForUser(User user, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("filedDate").descending());
        return claimRepository.findByPolicyPurchaseUser(user, pageable);
    }

    public Page<Claim> getAllClaimsFiltered(ClaimStatus status, String customerName, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("filedDate").descending());
        String nameFilter = (customerName != null && !customerName.isBlank()) ? customerName.trim() : null;
        return claimRepository.findWithFilters(status, nameFilter, pageable);
    }

    public long countByStatus(ClaimStatus status) {
        return claimRepository.countByStatus(status);
    }

    public Claim getClaimById(Long id) {
        return claimRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Claim not found with id: " + id));
    }

    public void markUnderReview(Long claimId) {
        Claim claim = getClaimById(claimId);
        claim.setStatus(ClaimStatus.UNDER_REVIEW);
        claimRepository.save(claim);

        emailService.sendEmail(
                claim.getPolicyPurchase().getUser().getEmail(),
                "Claim Under Review - " + claim.getPolicyPurchase().getPolicyNumber(),
                "Dear " + claim.getPolicyPurchase().getUser().getName() + ",\n\n" +
                        "Your claim (ID: " + claim.getId() + ") is now UNDER REVIEW by our agent team.\n\n" +
                        "- InsureFlow Team"
        );
    }

    public void approveClaim(Long claimId, String remarks) {
        Claim claim = getClaimById(claimId);
        claim.setStatus(ClaimStatus.APPROVED);
        claim.setRemarks(remarks);
        claimRepository.save(claim);

        emailService.sendEmail(
                claim.getPolicyPurchase().getUser().getEmail(),
                "Claim Approved - " + claim.getPolicyPurchase().getPolicyNumber(),
                "Dear " + claim.getPolicyPurchase().getUser().getName() + ",\n\n" +
                        "Good news! Your claim (ID: " + claim.getId() + ") of ₹" + claim.getClaimAmount() +
                        " has been APPROVED.\n\n" +
                        "Remarks: " + remarks + "\n\n" +
                        "- InsureFlow Team"
        );
    }

    public void rejectClaim(Long claimId, String remarks) {
        Claim claim = getClaimById(claimId);
        claim.setStatus(ClaimStatus.REJECTED);
        claim.setRemarks(remarks);
        claimRepository.save(claim);

        emailService.sendEmail(
                claim.getPolicyPurchase().getUser().getEmail(),
                "Claim Rejected - " + claim.getPolicyPurchase().getPolicyNumber(),
                "Dear " + claim.getPolicyPurchase().getUser().getName() + ",\n\n" +
                        "We're sorry to inform you that your claim (ID: " + claim.getId() + ") has been REJECTED.\n\n" +
                        "Remarks: " + remarks + "\n\n" +
                        "- InsureFlow Team"
        );
    }
}