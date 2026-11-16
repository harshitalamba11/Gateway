package in.project.gatekeeper.management.Services;

import in.project.gatekeeper.management.Dto.RequestDto.LoginRequestDto;
import in.project.gatekeeper.management.Dto.ResponseDto.LoginResponseDto;
import in.project.gatekeeper.management.Entities.ManagementUser;
import in.project.gatekeeper.management.Entities.OrganizationStatus;
import in.project.gatekeeper.management.Exceptions.ApiException;
import in.project.gatekeeper.management.Repository.ManagementUserRepository;
//import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;

@Service
public class AuthenticationService {
    ManagementUserRepository managementUserRepository;
    JwtEncoder jwtEncoder;
    PasswordEncoder passwordEncoder;
    @Value("${security.jwt.expiration-seconds}") private long expirationSeconds;
    AuthenticationService(JwtEncoder jwtEncoder, ManagementUserRepository managementUserRepository, PasswordEncoder passwordEncoder) {
        this.jwtEncoder = jwtEncoder;
        this.managementUserRepository = managementUserRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional(readOnly = true)
    public LoginResponseDto login(LoginRequestDto loginRequest) {
        String username = loginRequest.getUsername();
        String password = loginRequest.getPassword();

        ManagementUser user = managementUserRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        if(!user.isEnabled() || !passwordEncoder.matches(password, user.getPassword_hash())) {
            throw invalidCredentialsException();
        }

        if(user.getOrganization()!=null && !user.getOrganization().getStatus().equals(OrganizationStatus.ACTIVE)) {
            throw new ApiException(HttpStatus.FORBIDDEN, "Organization is not active");
        }

        Instant issueAt = Instant.now();
        String role = user.getRole().getName();

        JwtClaimsSet claims = JwtClaimsSet.builder()
                .issuer("self")
                .issuedAt(issueAt)
                .expiresAt(issueAt.plusSeconds(expirationSeconds))
                .subject(user.getUsername())
                .claim("role", role)
                .claim("organization_id", user.getOrganization() != null ? user.getOrganization().getId() : null)
                .build();

        String token = jwtEncoder.encode(JwtEncoderParameters.from(claims)).getTokenValue();
        Long organizationId = user.getOrganization() != null ? user.getOrganization().getId() : null;

        return new LoginResponseDto(token, "Bearer", issueAt, expirationSeconds, role, organizationId);


    }

    private RuntimeException invalidCredentialsException() {
        return new ApiException(HttpStatus.UNAUTHORIZED, "Invalid username or password");
    }
}
