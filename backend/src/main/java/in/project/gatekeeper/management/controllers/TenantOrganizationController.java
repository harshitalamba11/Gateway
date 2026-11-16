package in.project.gatekeeper.management.controllers;

import in.project.gatekeeper.management.Dto.RequestDto.TenantOrganizationRequest;
import in.project.gatekeeper.management.Dto.ResponseDto.OrganizationResponseDto;
import in.project.gatekeeper.management.Services.TenantOrganizationService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/tenant/organization")
public class TenantOrganizationController {
    @Autowired
    private TenantOrganizationService organizationService;

    @GetMapping
    public ResponseEntity<OrganizationResponseDto> getOrganization() {
        return ResponseEntity.ok(organizationService.getOrganization());
    }

    @PutMapping
    public ResponseEntity<OrganizationResponseDto> updateOrganization(@Valid @RequestBody TenantOrganizationRequest request){
        return ResponseEntity.ok(organizationService.updateOrganization(request));
    }
}
