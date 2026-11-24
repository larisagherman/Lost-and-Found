package backend.controller;

import backend.dto.AdminClaimResponse;
import backend.dto.ClaimDecisionRequest;
import backend.dto.ClaimResponse;
import backend.dto.ClaimSubmissionRequest;
import backend.model.ClaimStatus;
import backend.security.AppUserPrincipal;
import backend.service.ClaimRequestService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.core.annotation.AuthenticationPrincipal;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class ClaimRequestController {
    private final ClaimRequestService claimRequestService;

    @PostMapping("/item-reports/{reportId}/claims")
    @ResponseStatus(HttpStatus.CREATED)
    public ClaimResponse submitClaim(
            @PathVariable Long reportId,
            @Valid @RequestBody ClaimSubmissionRequest request,
            @AuthenticationPrincipal AppUserPrincipal principal
    ) {
        return claimRequestService.submitClaim(reportId, principal.getUserId(), request);
    }

    @GetMapping("/claims/mine")
    public List<ClaimResponse> getMyClaims(@AuthenticationPrincipal AppUserPrincipal principal) {
        return claimRequestService.getMyClaims(principal.getUserId());
    }

    @GetMapping("/admin/claims")
    public List<AdminClaimResponse> getInstitutionClaims(
            @RequestParam(required = false) ClaimStatus status,
            @AuthenticationPrincipal AppUserPrincipal principal
    ) {
        return claimRequestService.getInstitutionClaims(principal.getUserId(), status);
    }

    @PatchMapping("/admin/claims/{claimId}/decision")
    public AdminClaimResponse decideClaim(
            @PathVariable Long claimId,
            @Valid @RequestBody ClaimDecisionRequest request,
            @AuthenticationPrincipal AppUserPrincipal principal
    ) {
        return claimRequestService.decideClaim(
                claimId, principal.getUserId(), request.getStatus(), request.getReason()
        );
    }

    @PatchMapping("/admin/claims/{claimId}/complete")
    public AdminClaimResponse completeHandover(
            @PathVariable Long claimId,
            @AuthenticationPrincipal AppUserPrincipal principal
    ) {
        return claimRequestService.completeHandover(claimId, principal.getUserId());
    }

    @PatchMapping("/admin/claims/{claimId}/cancel-approval")
    public AdminClaimResponse cancelApproval(
            @PathVariable Long claimId,
            @AuthenticationPrincipal AppUserPrincipal principal
    ) {
        return claimRequestService.cancelApproval(claimId, principal.getUserId());
    }
}
