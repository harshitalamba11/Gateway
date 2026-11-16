package in.project.gatekeeper.management.Repository;

import in.project.gatekeeper.management.Dto.ResponseDto.OrganizationResponseDto;
import in.project.gatekeeper.management.Entities.Organization;
import in.project.gatekeeper.management.Entities.OrganizationStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OrganizationRepository extends JpaRepository<Organization, Long> {
    boolean existsByNameIgnoreCase(String name);
    boolean existsBySlugIgnoreCase(String slug);

    @Query("select o.id from Organization o where lower(o.name) = lower(:name)")
    Long getIdByName(@Param("name") String name);

    @Query("select o from Organization o where o.status != :status")
    List<OrganizationResponseDto> findByStatusNot(OrganizationStatus status);
}
