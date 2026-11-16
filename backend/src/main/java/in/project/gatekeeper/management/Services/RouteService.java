package in.project.gatekeeper.management.Services;

import in.project.gatekeeper.management.Dto.RequestDto.HeaderRuleRequest;
import in.project.gatekeeper.management.Dto.RequestDto.RouteRequest;
import in.project.gatekeeper.management.Dto.RequestDto.RouteStatusRequest;
import in.project.gatekeeper.management.Dto.ResponseDto.HeaderRuleResponse;
import in.project.gatekeeper.management.Dto.ResponseDto.RouteConfVersion;
import in.project.gatekeeper.management.Dto.ResponseDto.RouteResponse;
import in.project.gatekeeper.management.Entities.*;
import in.project.gatekeeper.management.Exceptions.ApiException;
import in.project.gatekeeper.management.Repository.ApiRouteRepository;
import in.project.gatekeeper.management.Repository.AuditRepository;
import in.project.gatekeeper.management.Repository.versionRepository;
import org.springframework.transaction.annotation.Transactional;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import tools.jackson.databind.ObjectMapper;

import java.net.URI;
import java.time.LocalDateTime;
import java.util.*;


@Service
public class RouteService {

    private final TenantContext tenantContext;
    private final ApiRouteRepository apiRouteRepository;
    private final ObjectMapper objectMapper;
    private final versionRepository versionRepository;
    private final AuditsService auditService;
    private final AuditRepository auditRepository;
    public RouteService (TenantContext tenantContext, ApiRouteRepository apiRouteRepository, ObjectMapper objectMapper, versionRepository versionRepository, AuditsService auditService, AuditRepository auditRepository){
        this.tenantContext = tenantContext;
        this.apiRouteRepository = apiRouteRepository;
        this.objectMapper = objectMapper;
        this.versionRepository = versionRepository;
        this.auditService = auditService;
        this.auditRepository = auditRepository;
    }

    @Transactional
    public RouteResponse create(@Valid RouteRequest request) {
        long startedAt = System.nanoTime();
        Organization organization = tenantContext.getOrganization();
        validate(request, organization);
        if(apiRouteRepository.existsByRouteIdAndOrganizationId(
                request.getRouteId(), organization.getId())) {

            throw new ApiException(
                    HttpStatus.CONFLICT,
                    "Route ID already exists for this organization");
        }

        ApiRoute route = new ApiRoute();
        route.setOrganization(organization);
        route.setCreatedAt(LocalDateTime.now());
        route.setConfigurationVersion(1L);
        apply(route, request);
        apiRouteRepository.save(route);
        saveVersion(route, "CREATED");

        auditService.record(organization, "CREATE_ROUTE",
                "API_ROUTE",route.getRouteId() , startedAt,
                "Route configuration version 1 created");

        return RouteResponse.from(route);
    }

    public void apply(ApiRoute route, RouteRequest request){
        route.setRouteId(request.getRouteId());
        route.setName(request.getName());
        route.setPath_pattern(request.getPathPattern());
        route.setTargetBaseUrl(request.getTargetBaseUrl());
        route.setTargetPathPrefix(request.getTargetPathPrefix());
        route.setRequestsPerMinute(request.getRequestsPerMinute());
        route.setResponseTimeoutMs(request.getResponseTimeoutMs());
        route.setIdempotencyEnabled(request.isIdempotencyEnabled());
        route.setActive(request.isActive());
        route.setAllowedMethods(normalizeMethods(request.getAllowedMethods()));

        route.getHeaderRules().clear();
        for(HeaderRuleRequest ruleRequest : request.getHeaderRules()) {
            RouterHeaderRule rule = new RouterHeaderRule();
            rule.setRoute(route);
            rule.setDirection(ruleRequest.getDirection().toUpperCase(Locale.ENGLISH));
            rule.setAction(ruleRequest.getAction().toUpperCase(Locale.ENGLISH));
            rule.setHeaderName(ruleRequest.getHeaderName());
            rule.setHeaderValue(ruleRequest.getHeaderValue());
            rule.setCreatedAt(LocalDateTime.now());
            rule.setUpdatedAt(LocalDateTime.now());
            route.getHeaderRules().add(rule);
        }
    }
    public void validate(RouteRequest request, Organization organization){
        String tenantPrefix = "/" + organization.getSlug() + "/";

        if(!request.getPathPattern().startsWith(tenantPrefix)){
            throw new ApiException(HttpStatus.BAD_REQUEST, "pathPattern must start with the tenant prefix "+ tenantPrefix);
        }

        if(!request.getTargetPathPrefix().startsWith("/")){
            throw new ApiException(HttpStatus.BAD_REQUEST, "targetPathPrefix must start with '/'");
        }

        try {
            URI uri = URI.create(request.getTargetBaseUrl());
            if(!"http".equalsIgnoreCase(uri.getScheme()) && !"https".equalsIgnoreCase(uri.getScheme())) {
                throw new IllegalArgumentException();
            }
        }
        catch(IllegalArgumentException exception) {
            throw new ApiException(HttpStatus.BAD_REQUEST,
                    "targetBaseUrl must be a valid HTTP or HTTPS URL");
        }

        normalizeMethods(request.getAllowedMethods());

        for(HeaderRuleRequest rule : request.getHeaderRules()) {
            String direction = rule.getDirection().toUpperCase(Locale.ENGLISH);
            String action = rule.getAction().toUpperCase(Locale.ENGLISH);
            if(!"REQUEST".equals(direction) && !"RESPONSE".equals(direction)) {
                throw new ApiException(HttpStatus.BAD_REQUEST,
                        "Header direction must be REQUEST or RESPONSE");
            }
            if(!"ADD".equals(action) && !"REMOVE".equals(action)) {
                throw new ApiException(HttpStatus.BAD_REQUEST,
                        "Header action must be ADD or REMOVE");
            }
            if("ADD".equals(action) &&
                    (rule.getHeaderValue() == null || rule.getHeaderValue().trim().isEmpty())) {
                throw new ApiException(HttpStatus.BAD_REQUEST, "ADD header rules require headerValue");
            }
        }

    }

    public Set<String> normalizeMethods(Set<String> methods){
        Set<String> normalized = new HashSet<>();
        for(String method : methods){
            String value = method.toUpperCase(Locale.ENGLISH);
            try{
                HttpMethod.valueOf(value);
            }
            catch(IllegalArgumentException ex){
                throw new ApiException(HttpStatus.BAD_REQUEST, "Unsupported HTTP method: "+ method);
            }
            normalized.add(value);
        }

        return normalized;
    }



    @Transactional(readOnly = true)
    public List<RouteResponse> findAllRoutes(){
        Organization organization  = tenantContext.getOrganization();
        List<RouteResponse> list = new ArrayList<>();

        List<ApiRoute> res = apiRouteRepository.findAllByOrganizationId(organization.getId());
        for(ApiRoute route : res){
            list.add(RouteResponse.from(route));
        }

        return list;

    }


    @Transactional(readOnly = true)
    public RouteResponse findById(String id){
        Organization organization = tenantContext.getOrganization();
        ApiRoute route = apiRouteRepository
                .findByRouteIdAndOrganizationId(
                        id, organization.getId())
                .orElseThrow(() -> new ApiException(
                        HttpStatus.NOT_FOUND, "Route not found"));
        return RouteResponse.from(route);
    }


    @Transactional
    public void delete(String id){
        long startedAt = System.nanoTime();
        Organization organization = tenantContext.getOrganization();
        ApiRoute route = apiRouteRepository.findByRouteIdAndOrganizationId(id, organization.getId()).orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Route not found"));
        route.setConfigurationVersion(route.getConfigurationVersion()+1);
        route.setUpdatedAt(LocalDateTime.now());
        saveVersion(route, "DELETED");

        auditService.record(organization, "DELETE_ROUTE", "API_ROUTE", route.getRouteId(), startedAt,
                "Final configuration saved as version " + route.getConfigurationVersion());

        apiRouteRepository.delete(route);
    }

    @Transactional
    public RouteResponse update(String id, @Valid RouteRequest request) {
        long startedAt = System.nanoTime();
        Organization organization = tenantContext.getOrganization();
        ApiRoute route = apiRouteRepository.findByRouteIdAndOrganizationId(id, organization.getId()).orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Route not found"));
        if(!route.getRouteId().equals(request.getRouteId())){
            throw new ApiException(HttpStatus.BAD_REQUEST, "routeId cannot be changed");
        }
        validate(request, organization);

        route.setConfigurationVersion(route.getConfigurationVersion() + 1);

        apply(route, request);
        saveVersion(route, "UPDATED");

        auditService.record(organization, "UPDATE_ROUTE", "API_ROUTE",
                route.getRouteId(), startedAt, "Route configuration version " +
                        route.getConfigurationVersion() + " created");


        return RouteResponse.from(route);
    }

    @Transactional
    public RouteResponse changeStatus(String id, RouteStatusRequest request){
        long startedAt = System.nanoTime();
        Organization organization = tenantContext.getOrganization();
        ApiRoute route = apiRouteRepository.findByRouteIdAndOrganizationId(id, organization.getId())
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Route not found"));
        route.setActive(request.getActive());
        route.setConfigurationVersion(route.getConfigurationVersion()+1);
        route.setUpdatedAt(LocalDateTime.now());

        String changeType = request.getActive() ? "ACTIVATED" : "DEACTIVATED";
        String action = request.getActive() ? "ACTIVATE_ROUTE" : "DEACTIVATE_ROUTE";
        saveVersion(route, changeType);

        auditService.record(organization, action, "API_ROUTE", route.getRouteId(), startedAt,
                "Route configuration version " + route.getConfigurationVersion() + " created");

        return RouteResponse.from(route);

    }

    @Transactional
    public List<RouteResponse> findAllActiveForGateway() {
        Organization organization = tenantContext.getOrganization();
        List<ApiRoute> routes = apiRouteRepository.getAllByActiveTrueAndOrganizationStatus(OrganizationStatus.ACTIVE);
        List<RouteResponse> list = new ArrayList<>();

        for(ApiRoute route : routes){
            list.add(RouteResponse.from(route));
        }

        return list;
    }

    public void saveVersion(ApiRoute route, String changeType){
        RouteConfigurationsVersion version = new RouteConfigurationsVersion();
        version.setChangeType(changeType);
        version.setConfigurationSnapshot(objectMapper.writeValueAsString(RouteResponse.from(route)));
        version.setCreatedAt(LocalDateTime.now());
        version.setVersionNumber(route.getConfigurationVersion());
        version.setRouteId(route.getRouteId());
        version.setOrganization(route.getOrganization());
        versionRepository.save(version);
    }

    public List<RouteConfVersion> getAllVersions(String routeId){
        List<RouteConfVersion> list = new ArrayList<>();
        Organization organization = tenantContext.getOrganization();
        List<RouteConfigurationsVersion> versions = versionRepository.findByOrganizationIdAndRouteIdOrderByVersionNumberDesc(organization.getId(), routeId);
        for(RouteConfigurationsVersion version : versions){
            list.add(RouteConfVersion.from(version));
        }
        return list;
    }


    public List<Audits> getAllAudits() {
        Organization organization = tenantContext.getOrganization();
        List<Audits> audit = auditRepository.findAllByOrganizationIdOrderByCreatedAtDesc(organization.getId());
        return audit;

    }
}
