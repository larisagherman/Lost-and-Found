package backend.service;

import backend.dto.AdminClaimResponse;
import backend.dto.ClaimResponse;
import backend.dto.ClaimSubmissionRequest;
import backend.model.ClaimRequest;
import backend.model.ClaimStatus;
import backend.model.ItemReport;
import backend.model.ItemReportStatus;
import backend.model.ItemReportType;
import backend.model.UserRole;
import backend.model.Users;
import backend.repository.ClaimRequestRepository;
import backend.repository.ItemReportRepository;
import backend.repository.UsersRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ClaimRequestService {
    private final ClaimRequestRepository claimRepository;
    private final ItemReportRepository itemReportRepository;
    private final UsersRepository usersRepository;
    private final ClaimNotificationService notificationService;

    @Transactional
    public ClaimResponse submitClaim(Long reportId, Long claimantId, ClaimSubmissionRequest request) {
        Users claimant = getUser(claimantId);
        if (claimant.getRole() != UserRole.STUDENT) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Only students can submit claims.");
        }

        ItemReport report = itemReportRepository.findByIdForUpdate(reportId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Found report not found."));
        if (report.getItemReportType() != ItemReportType.FOUND) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Claims can only be submitted for FOUND reports.");
        }
        if (report.getItemReportStatus() != ItemReportStatus.OPEN) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "This report is no longer open for claims.");
        }
        if (!claimant.getInstitution().getId().equals(report.getReporter().getInstitution().getId())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "You can only claim items from your institution.");
        }
        if (claimant.getId().equals(report.getReporter().getId())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "You cannot claim your own found report.");
        }
        if (report.getPrivateVerificationDetails() == null || report.getPrivateVerificationDetails().isBlank()) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "This report is not ready for online claims. Contact an institution administrator."
            );
        }
        if (claimRepository.existsByFoundItemReport_IdAndClaimant_Id(reportId, claimantId)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "You have already submitted a claim for this report.");
        }

        ClaimRequest claim = new ClaimRequest();
        claim.setFoundItemReport(report);
        claim.setClaimant(claimant);
        claim.setProofDetails(request.getProofDetails().trim());
        claim.setStatus(ClaimStatus.PENDING);
        try {
            claim = claimRepository.saveAndFlush(claim);
        } catch (DataIntegrityViolationException exception) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "You have already submitted a claim for this report.");
        }
        notificationService.notifySubmitted(claim);
        return toClaimResponse(claim);
    }

    @Transactional(readOnly = true)
    public List<ClaimResponse> getMyClaims(Long claimantId) {
        return claimRepository.findByClaimant_IdOrderByCreatedAtDesc(claimantId)
                .stream().map(this::toClaimResponse).toList();
    }

    @Transactional(readOnly = true)
    public List<AdminClaimResponse> getInstitutionClaims(Long adminId, ClaimStatus status) {
        Users admin = requireAdmin(adminId);
        List<ClaimRequest> claims = status == null
                ? claimRepository.findByFoundItemReport_Reporter_Institution_IdOrderByCreatedAtAsc(
                        admin.getInstitution().getId())
                : claimRepository.findByFoundItemReport_Reporter_Institution_IdAndStatusOrderByCreatedAtAsc(
                        admin.getInstitution().getId(), status);
        return claims.stream().map(this::toAdminResponse).toList();
    }

    @Transactional
    public AdminClaimResponse decideClaim(Long claimId, Long adminId, ClaimStatus decision, String reason) {
        Users admin = requireAdmin(adminId);
        if (decision != ClaimStatus.APPROVED && decision != ClaimStatus.REJECTED) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Decision must be APPROVED or REJECTED.");
        }
        ClaimRequest claim = getClaimForUpdate(claimId);
        ensureSameInstitution(admin, claim);
        if (claim.getStatus() != ClaimStatus.PENDING) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Only PENDING claims can be decided.");
        }
        ItemReport report = itemReportRepository.findByIdForUpdate(claim.getFoundItemReport().getId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Found report not found."));
        if (report.getItemReportStatus() != ItemReportStatus.OPEN) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "The found report is no longer open.");
        }
        if (decision == ClaimStatus.APPROVED
                && claimRepository.existsByFoundItemReport_IdAndStatus(report.getId(), ClaimStatus.APPROVED)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Another claim is already approved for this item.");
        }

        claim.setStatus(decision);
        claim.setReviewedByAdmin(admin);
        claim.setReviewedAt(LocalDateTime.now());
        claim.setAdminReason(normalizeReason(reason));
        claimRepository.save(claim);
        notificationService.notifyStatusChanged(claim, decision == ClaimStatus.APPROVED
                ? "Your claim was approved. An institution administrator will coordinate the handover."
                : "Your claim was rejected." + reasonSuffix(claim.getAdminReason()));
        return toAdminResponse(claim);
    }

    @Transactional
    public AdminClaimResponse completeHandover(Long claimId, Long adminId) {
        Users admin = requireAdmin(adminId);
        ClaimRequest claim = getClaimForUpdate(claimId);
        ensureSameInstitution(admin, claim);
        if (claim.getStatus() != ClaimStatus.APPROVED) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Only an APPROVED claim can be completed.");
        }
        ItemReport report = itemReportRepository.findByIdForUpdate(claim.getFoundItemReport().getId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Found report not found."));
        if (report.getItemReportStatus() != ItemReportStatus.OPEN) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "The found report is no longer open.");
        }

        LocalDateTime completedAt = LocalDateTime.now();
        claim.setStatus(ClaimStatus.COMPLETED);
        claim.setReviewedByAdmin(admin);
        claim.setCompletedAt(completedAt);
        claim.setReviewedAt(completedAt);
        claimRepository.save(claim);
        report.setItemReportStatus(ItemReportStatus.RESOLVED);
        itemReportRepository.save(report);

        List<ClaimRequest> remaining = claimRepository.findByFoundItemReport_IdAndStatus(
                report.getId(), ClaimStatus.PENDING);
        for (ClaimRequest other : remaining) {
            other.setStatus(ClaimStatus.CANCELLED);
            other.setReviewedByAdmin(admin);
            other.setReviewedAt(completedAt);
            other.setAdminReason("The item was handed over to another verified claimant.");
            claimRepository.save(other);
            notificationService.notifyStatusChanged(other, "Your claim was cancelled because the item was returned to another claimant.");
        }
        notificationService.notifyStatusChanged(claim, "The item handover was confirmed by an administrator.");
        return toAdminResponse(claim);
    }

    @Transactional
    public AdminClaimResponse cancelApproval(Long claimId, Long adminId) {
        Users admin = requireAdmin(adminId);
        ClaimRequest claim = getClaimForUpdate(claimId);
        ensureSameInstitution(admin, claim);
        if (claim.getStatus() != ClaimStatus.APPROVED) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Only an APPROVED claim can be cancelled.");
        }
        claim.setStatus(ClaimStatus.CANCELLED);
        claim.setReviewedByAdmin(admin);
        claim.setReviewedAt(LocalDateTime.now());
        claim.setAdminReason("The approved claimant did not complete pickup.");
        claimRepository.save(claim);
        notificationService.notifyStatusChanged(claim, "The approved claim was cancelled. The report remains open for other claims."
                + reasonSuffix(claim.getAdminReason()));
        return toAdminResponse(claim);
    }

    private ClaimRequest getClaimForUpdate(Long claimId) {
        return claimRepository.findByIdForUpdate(claimId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Claim not found."));
    }

    private Users getUser(Long userId) {
        return usersRepository.findById(userId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Signed-in user not found."));
    }

    private Users requireAdmin(Long userId) {
        Users user = getUser(userId);
        if (user.getRole() != UserRole.ADMIN) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Administrator access is required.");
        }
        return user;
    }

    private void ensureSameInstitution(Users admin, ClaimRequest claim) {
        if (!admin.getInstitution().getId().equals(
                claim.getFoundItemReport().getReporter().getInstitution().getId())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "You can only manage claims from your institution.");
        }
    }

    private String normalizeReason(String reason) {
        if (reason == null || reason.isBlank()) {
            return null;
        }
        return reason.trim();
    }

    private String reasonSuffix(String reason) {
        return reason == null ? "" : " Reason: " + reason;
    }

    private ClaimResponse toClaimResponse(ClaimRequest claim) {
        ClaimResponse response = new ClaimResponse();
        response.setId(claim.getId());
        response.setFoundItemReportId(claim.getFoundItemReport().getId());
        response.setItemName(claim.getFoundItemReport().getItemName());
        response.setItemLocation(claim.getFoundItemReport().getLocation());
        response.setProofDetails(claim.getProofDetails());
        response.setStatus(claim.getStatus());
        response.setAdminReason(claim.getAdminReason());
        response.setCreatedAt(claim.getCreatedAt());
        response.setReviewedAt(claim.getReviewedAt());
        response.setCompletedAt(claim.getCompletedAt());
        return response;
    }

    private AdminClaimResponse toAdminResponse(ClaimRequest claim) {
        AdminClaimResponse response = new AdminClaimResponse();
        ItemReport report = claim.getFoundItemReport();
        response.setId(claim.getId());
        response.setFoundItemReportId(report.getId());
        response.setItemName(report.getItemName());
        response.setPublicItemDetails(report.getItemDetails());
        response.setPrivateVerificationDetails(report.getPrivateVerificationDetails());
        response.setItemLocation(report.getLocation());
        response.setClaimantId(claim.getClaimant().getId());
        response.setClaimantName(claim.getClaimant().getDisplayName());
        response.setClaimantEmail(claim.getClaimant().getEmail());
        response.setProofDetails(claim.getProofDetails());
        response.setStatus(claim.getStatus());
        response.setAdminReason(claim.getAdminReason());
        response.setFinderId(report.getReporter().getId());
        response.setFinderName(report.getReporter().getDisplayName());
        response.setFinderEmail(report.getReporter().getEmail());
        response.setCreatedAt(claim.getCreatedAt());
        response.setReviewedAt(claim.getReviewedAt());
        response.setCompletedAt(claim.getCompletedAt());
        return response;
    }
}
