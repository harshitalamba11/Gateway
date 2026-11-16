package in.project.gatekeeper.management.controllers;

import in.project.gatekeeper.management.Dto.RequestDto.LoginRequestDto;
import in.project.gatekeeper.management.Dto.ResponseDto.LoginResponseDto;
import in.project.gatekeeper.management.Services.AuthenticationService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
public class AuthenticationController {

    private final AuthenticationService authenticationService;
    public AuthenticationController(AuthenticationService authenticationService) {
        this.authenticationService = authenticationService;
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponseDto> login(@Valid @RequestBody LoginRequestDto loginRequest) {
        // Implement your login logic here
        return ResponseEntity.ok(authenticationService.login(loginRequest));
    }
}
