package in.project.gatekeeper.management.Services;

import in.project.gatekeeper.management.Entities.Organization;
import in.project.gatekeeper.management.Entities.OrganizationStatus;
import in.project.gatekeeper.management.Exceptions.ApiException;
import in.project.gatekeeper.management.Repository.OrganizationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.stereotype.Component;

@Component
public class TenantContext {
    @Autowired
    private OrganizationRepository organizationRepository;

    public Organization getOrganization() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (!(authentication instanceof JwtAuthenticationToken)) {
            throw new ApiException(HttpStatus.FORBIDDEN, "Tenant identity missing");
        }

        Long organizationId = ((JwtAuthenticationToken) authentication).getToken().getClaim("organization_id");

        if(organizationId == null) {
            throw new ApiException(HttpStatus.FORBIDDEN, "Tenant identity missing");
        }

        Organization organization = organizationRepository.findById(organizationId).orElseThrow(() -> new ApiException(HttpStatus.FORBIDDEN, "Organization missing"));

        if(organization.getStatus() != OrganizationStatus.ACTIVE) {
            throw new ApiException(HttpStatus.FORBIDDEN, "Organization is not active");
        }
        return organization;
    }
}
