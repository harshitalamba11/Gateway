package in.project.gatekeeper.management.controllers;

//import in.project.gatekeeper.management.Entities.Organization;
import in.project.gatekeeper.management.Dto.RequestDto.OrganizationRequestDto;
import in.project.gatekeeper.management.Dto.RequestDto.OrganizationStatusRequestDto;
import in.project.gatekeeper.management.Dto.ResponseDto.OrganizationResponseDto;
import in.project.gatekeeper.management.Dto.ResponseDto.TenantAdminResponse;
import in.project.gatekeeper.management.Entities.OrganizationStatus;
import in.project.gatekeeper.management.Services.OrganizationService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import in.coderarmy.gatekeeper.management.dto.requestDto.TenantAdminRequest;
import java.util.List;

@RestController
@RequestMapping("/api/platform/organisation")
public class PlatformOrganisationController {

    private final OrganizationService organizationService;

    public PlatformOrganisationController(OrganizationService organizationService) {
        this.organizationService = organizationService;
    }

    @PostMapping("/create")
    public ResponseEntity<OrganizationResponseDto> createOrganisation(@Valid @RequestBody OrganizationRequestDto organizationRequestDto) {
        OrganizationResponseDto res = organizationService.create(organizationRequestDto);
        return ResponseEntity.ok(res);
    }

    @GetMapping("/{id}")
    public ResponseEntity<OrganizationResponseDto> getOrganizationByID(@PathVariable Long id) {
        OrganizationResponseDto res = organizationService.getOrganizationByID(id);
        return ResponseEntity.ok(res);

    }

    @GetMapping
    public ResponseEntity<List<OrganizationResponseDto>> getAllOrganizations() {
        return ResponseEntity.ok(organizationService.getAllOrganizations());
    }

    @PutMapping("/update/{id}")
    public ResponseEntity<OrganizationResponseDto> updateOrganization(@PathVariable Long id, @Valid @RequestBody OrganizationRequestDto organizationRequestDto) {
        OrganizationResponseDto res = organizationService.update(id, organizationRequestDto);
        return ResponseEntity.ok(res);
    }

    @PutMapping("/update-status/{id}")
    public ResponseEntity<OrganizationResponseDto> changeStatus(@PathVariable Long id, @Valid @RequestBody OrganizationStatusRequestDto status) {
        OrganizationResponseDto res = organizationService.changeStatus(id, status);
        return ResponseEntity.ok(res);
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<String> deleteOrganization(@PathVariable Long id) {
        organizationService.delete(id);
        return ResponseEntity.ok("Organization deleted successfully");
    }

//    CRUD --- TENENT_ADMIN
    @PostMapping("/{OrganizationId}/tenant-admin")
    public ResponseEntity<TenantAdminResponse> createTenantAdmin(@PathVariable Long OrganizationId, @Valid @RequestBody TenantAdminRequest tenantAdminRequest) {
        TenantAdminResponse res = organizationService.createTenantAdmin(OrganizationId, tenantAdminRequest);
        return ResponseEntity.ok(res);
    }

    @DeleteMapping("/{OrganizationId}/tenant-admin")
    public ResponseEntity<String> deleteTenantAdmin(@PathVariable Long OrganizationId) {
        organizationService.deleteTenantAdmin(OrganizationId);
        return ResponseEntity.ok("Tenant Admin deleted successfully");
    }

    @PutMapping("/{OrganizationId}/tenant-admin")
    public ResponseEntity<TenantAdminResponse> updateTenantAdmin(@PathVariable Long OrganizationId, @Valid @RequestBody TenantAdminRequest tenantAdminRequest) {
        TenantAdminResponse res = organizationService.updateTenantAdmin(OrganizationId, tenantAdminRequest);
        return ResponseEntity.ok(res);
    }

    @GetMapping("/{OrganizationId}/tenant-admin")
    public ResponseEntity<TenantAdminResponse> getTenantAdmin(@PathVariable Long OrganizationId) {
        TenantAdminResponse res = organizationService.getTenantAdmin(OrganizationId);
        return ResponseEntity.ok(res);
    }


}
