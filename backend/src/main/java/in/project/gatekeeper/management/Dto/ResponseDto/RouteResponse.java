package in.project.gatekeeper.management.Dto.ResponseDto;

import in.project.gatekeeper.management.Entities.ApiRoute;
import in.project.gatekeeper.management.Entities.RouterHeaderRule;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@AllArgsConstructor
@Getter
@Setter
public class RouteResponse {
    private Long id;
    private Long organizationId;
    private String routeId;
    private String name;
    private String pathPattern;
    private String targetBaseUrl;
    private String targetPathPrefix;
    private Integer requestsPerMinute;
    private Integer responseTimeoutMs;
    private boolean idempotencyEnabled;
    private boolean active;
    private Long configurationVersion;
    private Set<String> allowedMethods;
    private List<HeaderRuleResponse> headerRules;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public static RouteResponse from(ApiRoute route){
        List<HeaderRuleResponse> rules = new ArrayList<HeaderRuleResponse>();
        for(RouterHeaderRule rule : route.getHeaderRules()) {
            rules.add(HeaderRuleResponse.from(rule));
        }

        return new RouteResponse(route.getId(), route.getOrganization().getId(), route.getRouteId(),
                route.getName(), route.getPath_pattern(), route.getTargetBaseUrl(), route.getTargetPathPrefix(),
                route.getRequestsPerMinute(), route.getResponseTimeoutMs(), route.isIdempotencyEnabled(),
                route.isActive(), route.getConfigurationVersion(), route.getAllowedMethods(), rules,
                route.getCreatedAt(), route.getUpdatedAt());
    }
}
