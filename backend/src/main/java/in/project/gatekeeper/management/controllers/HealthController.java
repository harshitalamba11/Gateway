package in.project.gatekeeper.management.controllers;

import in.project.gatekeeper.management.Dto.ResponseDto.HealthResponseDto;
import in.project.gatekeeper.management.Services.HealthService;
//import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;

@RestController
@RequestMapping("/health")
public class HealthController {

//    private String serviceName;
//    private String serviceDescription;
//
//    public HealthController(@Value("${info.app.name}") String serviceName, @Value("${info.app.description}") String serviceDescription){
//        this.serviceDescription = serviceDescription;
//        this.serviceName = serviceName;
//    }

    public HealthService healthService;

    public HealthController(HealthService healthService) {
        this.healthService = healthService;
    }

    @GetMapping
    public ResponseEntity<HealthResponseDto> heath(){
        String dbStatus = healthService.getDbStatus();
        return ResponseEntity.ok(new HealthResponseDto("Up", dbStatus, LocalDateTime.now()));
    }
}
