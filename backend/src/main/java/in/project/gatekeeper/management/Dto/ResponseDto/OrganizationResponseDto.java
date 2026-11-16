package in.project.gatekeeper.management.Dto.ResponseDto;

import in.project.gatekeeper.management.Entities.Organization;
import in.project.gatekeeper.management.Entities.OrganizationStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Setter
@Getter
public class OrganizationResponseDto {
    private String name;

    private String slug;

    private OrganizationStatus status;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public OrganizationResponseDto(String name, String slug, OrganizationStatus status, LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.name = name;
        this.slug = slug;
        this.status = status;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public static OrganizationResponseDto from(Organization organization) {
        return new OrganizationResponseDto(
                organization.getName(),
                organization.getSlug(),
                organization.getStatus(),
                organization.getCreatedAt(),
                organization.getUpdatedAt()
        );
    }
}
