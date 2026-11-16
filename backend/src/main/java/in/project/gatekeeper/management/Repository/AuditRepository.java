package in.project.gatekeeper.management.Repository;

import in.project.gatekeeper.management.Entities.Audits;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AuditRepository extends JpaRepository<Audits, Long> {
    public List<Audits> findAllByOrganizationIdOrderByCreatedAtDesc(Long id);
}
