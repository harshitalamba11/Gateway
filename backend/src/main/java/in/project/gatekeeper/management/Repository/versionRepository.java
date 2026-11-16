package in.project.gatekeeper.management.Repository;

import in.project.gatekeeper.management.Entities.Organization;
import in.project.gatekeeper.management.Entities.RouteConfigurationsVersion;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface versionRepository extends JpaRepository<RouteConfigurationsVersion, Long> {
    List<RouteConfigurationsVersion> findByOrganizationIdAndRouteIdOrderByVersionNumberDesc(Long OrganizationId, String routeId);
}
