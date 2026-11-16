package in.project.gatekeeper.management.Services;

import in.project.gatekeeper.management.Entities.Audits;
import in.project.gatekeeper.management.Entities.Organization;
import in.project.gatekeeper.management.Repository.AuditRepository;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.time.LocalDateTime;

@Service
public class AuditsService {
    private final AuditRepository auditrepo;
    public AuditsService(AuditRepository auditrepo){
        this.auditrepo = auditrepo;
    }
    public void record(Organization organization, String action, String resourceType, String resourceId, Long startedAt, String details){
        Audits record = new Audits();
        record.setOrganization(organization);
        record.setAction(action);
        record.setResourceId(resourceId);
        record.setCorrelationId(getCorrelationId());
        record.setCreatedAt(LocalDateTime.now());
        record.setDetails(details);
        record.setResourceType(resourceType);
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String actor = auth == null ? "System" : auth.getName();
        record.setActor(actor);
        record.setResult("SUCCESS");
        record.setDurationMs((System.nanoTime() - startedAt) / 1000000);
        auditrepo.save(record);
    }

    private String getCorrelationId() {
        if(!(RequestContextHolder.getRequestAttributes()
                instanceof ServletRequestAttributes)) {
            return null;
        }
        HttpServletRequest request = ((ServletRequestAttributes)
                RequestContextHolder.getRequestAttributes())
                .getRequest();
        return request.getHeader("X-Correlation-ID");
    }
}
