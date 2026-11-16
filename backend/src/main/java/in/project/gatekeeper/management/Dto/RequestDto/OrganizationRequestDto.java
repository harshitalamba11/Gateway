package in.project.gatekeeper.management.Dto.RequestDto;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@AllArgsConstructor
public class OrganizationRequestDto {

    @NotBlank
    @Size(max = 150)
    private String name;

    @NotBlank
    @Size(max = 100)
    @Pattern(regexp = "[a-z0-9]+(?:-[a-z0-9]+)*$", message = "Slug must be lowercase, alphanumeric, and can contain hyphens")
    private String slug;
}
