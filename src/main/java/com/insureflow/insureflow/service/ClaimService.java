package com.insureflow.insureflow.service;

import com.insureflow.insureflow.dto.ClaimRequest;
import com.insureflow.insureflow.entity.*;
import com.insureflow.insureflow.exception.ResourceNotFoundException;
import com.insureflow.insureflow.repository.ClaimRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
public class ClaimService {

    private final ClaimRepository claimRepository;

    public ClaimService(ClaimRepository claimRepository) {
        this.claimRepository = claimRepository;
    }

    public Claim fileClaim(PolicyPurchase purchase, ClaimRequest request) {
        Claim claim = new Claim();
        claim.setPolicyPurchase(purchase);
        claim.setClaimAmount(request.getClaimAmount());
        claim.setReason(request.getReason());
        claim.setStatus(ClaimStatus.PENDING);
        claim.setFiledDate(LocalDate.now());
        return claimRepository.save(claim);
    }

    public List<Claim> getClaimsForUser(User user) {
        return claimRepository.findByPolicyPurchaseUser(user);
    }

    public List<Claim> getAllClaims() {
        return claimRepository.findAll();
    }

    public Claim getClaimById(Long id) {
        return claimRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Claim not found with id: " + id));
    }

    public void markUnderReview(Long claimId) {
        Claim claim = getClaimById(claimId);
        claim.setStatus(ClaimStatus.UNDER_REVIEW);
        claimRepository.save(claim);
    }

    public void approveClaim(Long claimId, String remarks) {
        Claim claim = getClaimById(claimId);
        claim.setStatus(ClaimStatus.APPROVED);
        claim.setRemarks(remarks);
        claimRepository.save(claim);
    }

    public void rejectClaim(Long claimId, String remarks) {
        Claim claim = getClaimById(claimId);
        claim.setStatus(ClaimStatus.REJECTED);
        claim.setRemarks(remarks);
        claimRepository.save(claim);
    }
}