package in.project.gatekeeper.management.Dto.ResponseDto;

import in.project.gatekeeper.management.Entities.RouteConfigurationsVersion;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
public class RouteConfVersion {
    private Long id;
    private String routeId;
    private Long versionNumber;
    private String changeType;
    private String configurationSnapshot;
    private LocalDateTime createdAt;

    public static RouteConfVersion from(RouteConfigurationsVersion version) {
        return new RouteConfVersion(version.getId(), version.getRouteId(), version.getVersionNumber(),
                version.getChangeType(), version.getConfigurationSnapshot(), version.getCreatedAt());
    }
}
