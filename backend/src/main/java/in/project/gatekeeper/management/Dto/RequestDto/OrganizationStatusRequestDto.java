package in.project.gatekeeper.management.Dto.RequestDto;

import in.project.gatekeeper.management.Entities.OrganizationStatus;
import jakarta.persistence.GeneratedValue;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class OrganizationStatusRequestDto {
    @NotNull
    private OrganizationStatus status;
}
