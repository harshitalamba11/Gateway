package in.project.gatekeeper.management.Repository;

import in.project.gatekeeper.management.Entities.ApiRoute;
import in.project.gatekeeper.management.Entities.OrganizationStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ApiRouteRepository extends JpaRepository<ApiRoute, Long> {
    public boolean existsByRouteIdAndOrganizationId(String RouteId, Long OrganizationId);
    public List<ApiRoute> findAllByOrganizationId(Long organizationId);
    Optional<ApiRoute> findByRouteIdAndOrganizationId(
            String routeId, Long organizationId);
    public List<ApiRoute> getAllByActiveTrueAndOrganizationStatus(OrganizationStatus status);
}
