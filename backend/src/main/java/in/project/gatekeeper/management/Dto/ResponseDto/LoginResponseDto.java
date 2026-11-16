package in.project.gatekeeper.management.Dto.ResponseDto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;

@Setter
@Getter
@AllArgsConstructor
public class LoginResponseDto {
    private String token;
    private String tokenType;
    private Instant issuedAt;
    private long expiresIn;
    private String role;
    private Long organizationId;
}
