package in.project.gatekeeper.management.Dto.RequestDto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@AllArgsConstructor
public class LoginRequestDto {
    @NotBlank
    private String username;
    @NotBlank
    private String password;
}
