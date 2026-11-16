package in.project.gatekeeper.management.Services;

import in.project.gatekeeper.management.Dto.RequestDto.TenantOrganizationRequest;
import in.project.gatekeeper.management.Dto.ResponseDto.OrganizationResponseDto;
import in.project.gatekeeper.management.Entities.Organization;
import in.project.gatekeeper.management.Exceptions.ApiException;
import in.project.gatekeeper.management.Repository.OrganizationRepository;
import jakarta.transaction.Transactional;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class TenantOrganizationService {

    private final TenantContext tenantContext;
    private final OrganizationRepository organizationRepository;
    private final AuditsService auditService;
    TenantOrganizationService(TenantContext tenantContext, OrganizationRepository organizationRepository, AuditsService auditService){
        this.tenantContext = tenantContext;
        this.organizationRepository = organizationRepository;
        this.auditService = auditService;
    }

    public OrganizationResponseDto getOrganization(){
        return OrganizationResponseDto.from(tenantContext.getOrganization());
    }

    @Transactional
    public OrganizationResponseDto updateOrganization(TenantOrganizationRequest request){
        long startedAt = System.nanoTime();
        Organization organization = tenantContext.getOrganization();

        if(!organization.getName().equalsIgnoreCase(request.getName()) && organizationRepository.existsByNameIgnoreCase(request.getName())){
            throw new ApiException(HttpStatus.CONFLICT, "Organization name already exists");
        }

        organization.setName(request.getName());
        organization.setUpdatedAt(LocalDateTime.now());

        auditService.record(organization, "UPDATE_OWN_ORGANIZATION",
                "ORGANIZATION", organization.getId().toString(), startedAt,
                "Tenant updated its organization name");

        return OrganizationResponseDto.from(organization);
    }

}
