package in.project.gatekeeper.management.Dto.RequestDto;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RouteStatusRequest {

    @NotNull
    private Boolean active;
}