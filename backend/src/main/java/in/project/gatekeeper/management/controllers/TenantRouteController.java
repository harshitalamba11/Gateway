package in.project.gatekeeper.management.controllers;

import in.project.gatekeeper.management.Dto.RequestDto.RouteRequest;
import in.project.gatekeeper.management.Dto.RequestDto.RouteStatusRequest;
import in.project.gatekeeper.management.Dto.ResponseDto.RouteConfVersion;
import in.project.gatekeeper.management.Dto.ResponseDto.RouteResponse;
import in.project.gatekeeper.management.Entities.Audits;
import in.project.gatekeeper.management.Services.RouteService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/tenant")
public class TenantRouteController {
    private final RouteService routeService;

    public TenantRouteController(RouteService routeService) {
        this.routeService = routeService;
    }

    @PostMapping("/routes")
    public ResponseEntity<RouteResponse> create(@Valid @RequestBody RouteRequest request){
        return ResponseEntity.status(HttpStatus.CREATED).body(routeService.create(request));
    }

    @GetMapping("/routes/{id}")
    public ResponseEntity<RouteResponse> findById(@PathVariable String id) {
        return ResponseEntity.ok(routeService.findById(id));
    }

    @GetMapping("/routes")
    public ResponseEntity<List<RouteResponse>> findAll() {
        return ResponseEntity.ok(routeService.findAllRoutes());
    }

    @PutMapping("/routes/{id}")
    public ResponseEntity<RouteResponse> update(@PathVariable String id,
                                                @Valid @RequestBody RouteRequest request) {
        return ResponseEntity.ok(routeService.update(id, request));
    }

    @DeleteMapping("/routes/{id}")
    public ResponseEntity<Void> delete(@PathVariable String id){
        routeService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/routes/{id}/status")
    public ResponseEntity<RouteResponse> changeStatus(@PathVariable String id,
                                                      @Valid @RequestBody RouteStatusRequest request) {
        return ResponseEntity.ok(routeService.changeStatus(id, request));
    }

    @GetMapping("/route-versions/{routeId}")
    public ResponseEntity<List<RouteConfVersion>> getAllVersions(@PathVariable String routeId){
        return ResponseEntity.ok(routeService.getAllVersions(routeId));
    }

    @GetMapping("/audit-records")
    public ResponseEntity<List<Audits>> getAudits(){
        return ResponseEntity.ok(routeService.getAllAudits());
    }
}
