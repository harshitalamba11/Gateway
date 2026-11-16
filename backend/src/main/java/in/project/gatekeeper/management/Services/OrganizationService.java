package in.project.gatekeeper.management.Services;

import ch.qos.logback.classic.encoder.JsonEncoder;
import in.project.gatekeeper.management.Dto.RequestDto.OrganizationRequestDto;
import in.project.gatekeeper.management.Dto.RequestDto.OrganizationStatusRequestDto;
import in.project.gatekeeper.management.Dto.ResponseDto.OrganizationResponseDto;
import in.project.gatekeeper.management.Dto.ResponseDto.TenantAdminResponse;
import in.project.gatekeeper.management.Entities.ManagementUser;
import in.project.gatekeeper.management.Entities.Organization;
import in.project.gatekeeper.management.Entities.OrganizationStatus;
import in.project.gatekeeper.management.Entities.Role;
import in.project.gatekeeper.management.Exceptions.ApiException;
import in.project.gatekeeper.management.Repository.ManagementUserRepository;
import in.project.gatekeeper.management.Repository.OrganizationRepository;
import in.project.gatekeeper.management.Repository.RoleRepository;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import in.coderarmy.gatekeeper.management.dto.requestDto.TenantAdminRequest;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class OrganizationService {
    private final OrganizationRepository organizationRepository;
    private final ManagementUserRepository managementUserRepository;
    private final RoleRepository roleRespository;
    private final PasswordEncoder passwordEncoder;
    private final AuditsService auditsService;
    public OrganizationService(OrganizationRepository organizationRepository, ManagementUserRepository managementUserRepository, RoleRepository roleRepository, PasswordEncoder passwordEncoder, AuditsService auditsService) {
        this.organizationRepository = organizationRepository;
        this.managementUserRepository = managementUserRepository;
        this.roleRespository = roleRepository;
        this.passwordEncoder = passwordEncoder;
        this.auditsService =  auditsService;
    }

    @Transactional
    public OrganizationResponseDto create(OrganizationRequestDto organizationRequestDto) {
        long startedAt = System.nanoTime();
        String name = organizationRequestDto.getName();
        String slug = organizationRequestDto.getSlug();
        Long id = organizationRepository.getIdByName(name);
        checkUnique(name, slug, id);

        LocalDateTime now = LocalDateTime.now();
        Organization organization = new Organization();
        organization.setName(name);
        organization.setSlug(slug);
        organization.setCreatedAt(now);
        organization.setStatus(OrganizationStatus.PENDING);
        organization.setUpdatedAt(now);
        organizationRepository.save(organization);

        auditsService.record(organization, "CREATE_ORGANIZATION", "ORGANIZATION",
                organization.getId().toString(), startedAt,
                "Organization created in PENDING status");

        return new OrganizationResponseDto(
                organization.getName(),
                organization.getSlug(),
                organization.getStatus(),
                organization.getCreatedAt(),
                organization.getUpdatedAt()
        );
    }

    public void checkUnique(String name, String slug, Long current_id) {
        if(organizationRepository.existsByNameIgnoreCase(name)){
            Organization current = current_id==null ? null : organizationRepository.findById(current_id).orElse(null);
            if(current==null || !current.getName().equalsIgnoreCase(name)){
                throw new RuntimeException("Organization name already exists");
            }
        }
        if(organizationRepository.existsBySlugIgnoreCase(slug)){
            Organization current = current_id==null ? null : organizationRepository.findById(current_id).orElse(null);
            if(current==null || !current.getSlug().equalsIgnoreCase(slug)){
                throw new RuntimeException("Organization slug already exists");
            }
        }
    }

    public OrganizationResponseDto getOrganizationByID(Long id) {
        Organization organization = organizationRepository.findById(id).orElseThrow(() -> new RuntimeException("Organization not found"));
//        return new OrganizationResponseDto(
//                organization.getName(),
//                organization.getSlug(),
//                organization.getStatus(),
//                organization.getCreatedAt(),
//                organization.getUpdatedAt()
//        );
        return OrganizationResponseDto.from(organization);
    }

    public List<OrganizationResponseDto> getAllOrganizations() {

        List<OrganizationResponseDto> organizationsList = new ArrayList<>();

        for (OrganizationResponseDto organization :
                organizationRepository.findByStatusNot(OrganizationStatus.INACTIVE)) {

//            OrganizationResponseDto dto = OrganizationResponseDto.from(organization);

//            organizationsList.add(dto);
            organizationsList.add(organization);

        }

        return organizationsList;
    }

    @Transactional
    public OrganizationResponseDto update(Long id, OrganizationRequestDto organizationRequestDto){
        long startedAt = System.nanoTime();
        Organization organization = organizationRepository.findById(id).orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Organization not found"));

        organization.setName(organizationRequestDto.getName());
        organization.setSlug(organizationRequestDto.getSlug());
        organization.setUpdatedAt(LocalDateTime.now());

        auditsService.record(organization, "UPDATE_ORGANIZATION",
                "ORGANIZATION", id.toString(), startedAt,
                "Organization name and slug updated");

        return OrganizationResponseDto.from(organization);

    }

    @Transactional
    public OrganizationResponseDto changeStatus(Long id, OrganizationStatusRequestDto status) {
        long startedAt = System.nanoTime();
        Organization organization = organizationRepository.findById(id).orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Organization not found"));

        if(organization.getStatus() == OrganizationStatus.INACTIVE){
            throw new ApiException(HttpStatus.BAD_REQUEST, "Cannot change status of an inactive organization");
        }
        organization.setStatus(status.getStatus());
        organization.setUpdatedAt(LocalDateTime.now());

        auditsService.record(organization, "CHANGE_ORGANIZATION_STATUS",
                "ORGANIZATION", id.toString(),
                startedAt, "Status changed to " + status.getStatus());

        return OrganizationResponseDto.from(organization);
    }

    @Transactional
    public void delete(Long id) {
        Organization organization = organizationRepository.findById(id).orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Organization not found"));
        long startedAt = System.nanoTime();

        if(organization.getStatus() == OrganizationStatus.INACTIVE){
            throw new ApiException(HttpStatus.BAD_REQUEST, "Organization is already inactive");
        }

        organization.setStatus(OrganizationStatus.INACTIVE);
        organization.setDeletedAt(LocalDateTime.now());
        organization.setUpdatedAt(LocalDateTime.now());

        auditsService.record(organization, "DELETE_ORGANIZATION",
                "ORGANIZATION", id.toString(), startedAt,
                "Organization soft-deleted");
    }

    @Transactional
    public TenantAdminResponse createTenantAdmin(Long id, @Valid TenantAdminRequest request){
        long startedAt = System.nanoTime();
        Organization organization = organizationRepository.findById(id).orElseThrow(()-> new ApiException(HttpStatus.NOT_FOUND, "Organization not found"));

        if(managementUserRepository.findByOrganizationId(id).isPresent()){
            throw new ApiException(HttpStatus.BAD_REQUEST, "Tenant admin already exists for this organization");
        }

        if(organization.getStatus()!= OrganizationStatus.ACTIVE){
            throw new ApiException(HttpStatus.BAD_REQUEST, "Cannot create tenant admin for an inactive organization");
        }

        if(managementUserRepository.existsByUsername(request.getUsername())){
            throw new ApiException(HttpStatus.BAD_REQUEST, "Username already exists");
        }

        Role role = roleRespository.findByName("TENANT_ADMIN").orElseThrow(()-> new ApiException(HttpStatus.NOT_FOUND, "Role not found"));

        ManagementUser user = new ManagementUser();
        user.setOrganization(organization);
        user.setRole(role);
        user.setUsername(request.getUsername());
        user.setPassword_hash(passwordEncoder.encode(request.getPassword()));
        user.setEnabled(true);
        user.setCreatedAt(LocalDateTime.now());
        user.setUpdatedAt(LocalDateTime.now());
        managementUserRepository.save(user);

        auditsService.record(organization, "CREATE_TENANT_ADMIN",
                "MANAGEMENT_USER", user.getId().toString(),
                startedAt, "Tenant administrator created");

        return TenantAdminResponse.from(user);

    }

    @Transactional
    public void deleteTenantAdmin(Long organizationId){
        long startedAt = System.nanoTime();
        ManagementUser user = managementUserRepository.findByOrganizationId(organizationId).orElseThrow(()-> new ApiException(HttpStatus.NOT_FOUND, "Tenant admin not found for this organization"));
        managementUserRepository.delete(user);

        auditsService.record(user.getOrganization(), "DELETE_TENANT_ADMIN",
                "MANAGEMENT_USER", user.getId().toString(), startedAt,
                "Tenant administrator deleted");
    }

    @Transactional
    public TenantAdminResponse updateTenantAdmin(Long organizationId, @Valid TenantAdminRequest request){
        long startedAt = System.nanoTime();

        ManagementUser user = managementUserRepository.findByOrganizationId(organizationId).orElseThrow(()-> new ApiException(HttpStatus.NOT_FOUND, "Tenant admin not found for this organization"));

        if(!user.getUsername().equals(request.getUsername()) && managementUserRepository.existsByUsername(request.getUsername())){
            throw new ApiException(HttpStatus.BAD_REQUEST, "Username already exists");
        }

        user.setUsername(request.getUsername());
        user.setPassword_hash(passwordEncoder.encode(request.getPassword()));
        user.setUpdatedAt(LocalDateTime.now());

        auditsService.record(user.getOrganization(), "UPDATE_TENANT_ADMIN",
                "MANAGEMENT_USER", user.getId().toString(), startedAt,
                "Tenant administrator updated");

        return TenantAdminResponse.from(user);
    }

    public TenantAdminResponse getTenantAdmin(Long organizationId){
        ManagementUser user = managementUserRepository.findByOrganizationId(organizationId).orElseThrow(()-> new ApiException(HttpStatus.NOT_FOUND, "Tenant admin not found for this organization"));
        return TenantAdminResponse.from(user);
    }


}