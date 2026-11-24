package backend.service;

import backend.dto.ItemReportDtoRequest;
import backend.dto.ItemReportDtoResponse;
import backend.mapper.ItemReportMapper;
import backend.model.ItemReport;
import backend.model.ItemReportStatus;
import backend.model.ItemReportType;
import backend.model.Users;
import backend.repository.ItemReportRepository;
import backend.repository.ClaimRequestRepository;
import backend.repository.UsersRepository;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
@AllArgsConstructor
public class ItemReportService {
    private final ItemReportRepository itemReportRepository;
    private final UsersRepository usersRepository;
    private final ItemReportMapper itemReportMapper;
    private final ClaimRequestRepository claimRequestRepository;
    public void createItemReport(ItemReportDtoRequest itemReportDtoRequest, Long reporterId) {
        Users reporter = usersRepository.findById(reporterId)
                .orElseThrow(() -> new IllegalArgumentException("Reporter not found."));
        ItemReport itemReport=new ItemReport();
        itemReport.setReporter(reporter);
        validateReportTypeAndPrivateDetails(itemReportDtoRequest);
        itemReport.setItemName(itemReportDtoRequest.getItemName());
        itemReport.setItemDetails(itemReportDtoRequest.getItemDetails());
        itemReport.setPrivateVerificationDetails(
                itemReportDtoRequest.getItemReportType() == ItemReportType.FOUND
                        ? itemReportDtoRequest.getPrivateVerificationDetails().trim()
                        : null
        );
        itemReport.setItemReportType(itemReportDtoRequest.getItemReportType());
        itemReport.setLocation(itemReportDtoRequest.getLocation());
        itemReport.setIncidentAt(itemReportDtoRequest.getIncidentAt());
        itemReport.setItemDetails(itemReportDtoRequest.getItemDetails());
        itemReport.setCategory(itemReportDtoRequest.getCategory());
        itemReport.setColour(itemReportDtoRequest.getColour());
        itemReport.setBrand(itemReportDtoRequest.getBrand());
        itemReport.setIncidentAt(itemReportDtoRequest.getIncidentAt());
        itemReportRepository.save(itemReport);
    }
    public List<ItemReportDtoResponse> getAllItemReports(
            Long institutionId) {

        List<ItemReport> itemReports =
                itemReportRepository
                        .findByReporterInstitutionIdAndItemReportStatus(
                                institutionId, ItemReportStatus.OPEN);

        return itemReportMapper
                .toItemReportDtoResponseList(itemReports);
    }
    public List<ItemReportDtoResponse> getAllLostItemReports(Long institutionId) {
        List<ItemReport> itemReports =
                itemReportRepository.findByReporterInstitutionIdAndItemReportTypeAndItemReportStatus(
                        institutionId,
                        ItemReportType.LOST,
                        ItemReportStatus.OPEN
                );

        return itemReportMapper.toItemReportDtoResponseList(itemReports);
    }
    public List<ItemReportDtoResponse> getAllFoundItemReports(Long institutionId) {
        List<ItemReport> itemReports =
                itemReportRepository.findByReporterInstitutionIdAndItemReportTypeAndItemReportStatus(
                        institutionId,
                        ItemReportType.FOUND,
                        ItemReportStatus.OPEN
                );

        return itemReportMapper.toItemReportDtoResponseList(itemReports);
    }

    public List<ItemReportDtoResponse> getMyItemReports(Long reporterId) {
        return itemReportMapper.toItemReportDtoResponseList(
                itemReportRepository.findByReporterIdOrderByCreatedAtDesc(reporterId)
        );
    }

    public ItemReportDtoResponse updateMyItemReport(
            Long itemReportId,
            Long reporterId,
            ItemReportDtoRequest request
    ) {
        ItemReport report = getOwnedOpenReport(itemReportId, reporterId);
        if (claimRequestRepository.existsByFoundItemReport_IdAndStatusIn(
                itemReportId, List.of(backend.model.ClaimStatus.PENDING, backend.model.ClaimStatus.APPROVED))) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "A report cannot be edited while a claim is awaiting review or handover."
            );
        }
        validateReportTypeAndPrivateDetails(request);
        if (report.getItemReportType() != request.getItemReportType()
                && claimRequestRepository.existsByFoundItemReport_Id(itemReportId)) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "A report with a submitted claim cannot change between LOST and FOUND."
            );
        }
        report.setItemName(request.getItemName());
        report.setItemDetails(request.getItemDetails());
        report.setPrivateVerificationDetails(request.getItemReportType() == ItemReportType.FOUND
                ? request.getPrivateVerificationDetails().trim()
                : null);
        report.setCategory(request.getCategory());
        report.setColour(request.getColour());
        report.setBrand(request.getBrand());
        report.setLocation(request.getLocation());
        report.setIncidentAt(request.getIncidentAt());
        report.setItemReportType(request.getItemReportType());
        return itemReportMapper.toItemReportDtoResponse(itemReportRepository.save(report));
    }

    public ItemReportDtoResponse changeMyItemReportStatus(
            Long itemReportId,
            Long reporterId,
            ItemReportStatus newStatus
    ) {
        if (newStatus != ItemReportStatus.RESOLVED && newStatus != ItemReportStatus.CLOSED) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "You can change an open report to RESOLVED or CLOSED."
            );
        }
        ItemReport report = getOwnedOpenReport(itemReportId, reporterId);
        if (newStatus == ItemReportStatus.RESOLVED && report.getItemReportType() == ItemReportType.FOUND) {
            throw new ResponseStatusException(
                    HttpStatus.FORBIDDEN,
                    "An administrator confirms a found-item handover and resolves the report."
            );
        }
        if (claimRequestRepository.existsByFoundItemReport_IdAndStatusIn(
                itemReportId, List.of(backend.model.ClaimStatus.PENDING, backend.model.ClaimStatus.APPROVED))) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "A report cannot be closed while a claim is awaiting review or handover."
            );
        }
        report.setItemReportStatus(newStatus);
        return itemReportMapper.toItemReportDtoResponse(itemReportRepository.save(report));
    }

    private ItemReport getOwnedOpenReport(Long itemReportId, Long reporterId) {
        ItemReport report = itemReportRepository.findById(itemReportId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Report not found."));
        if (!report.getReporter().getId().equals(reporterId)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "You can only manage your own reports.");
        }
        if (report.getItemReportStatus() != ItemReportStatus.OPEN) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "Only OPEN reports can be edited or have their status changed."
            );
        }
        return report;
    }

    private void validateReportTypeAndPrivateDetails(ItemReportDtoRequest request) {
        if (request.getItemReportType() == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Report type is required.");
        }
        if (request.getItemReportType() == ItemReportType.FOUND
                && (request.getPrivateVerificationDetails() == null
                || request.getPrivateVerificationDetails().isBlank())) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "FOUND reports require private verification details for claim review."
            );
        }
    }
}
