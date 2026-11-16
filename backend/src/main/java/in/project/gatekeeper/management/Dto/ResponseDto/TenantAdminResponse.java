package in.project.gatekeeper.management.Dto.ResponseDto;

import in.project.gatekeeper.management.Entities.ManagementUser;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class TenantAdminResponse {
    private String username;
    private String organizationName;
    private boolean enabled;

    public static TenantAdminResponse from(ManagementUser managementUser) {
        TenantAdminResponse response = new TenantAdminResponse();
        response.setUsername(managementUser.getUsername());
        response.setOrganizationName(managementUser.getOrganization().getName());
        response.setEnabled(managementUser.isEnabled());
        return response;
    }
}
