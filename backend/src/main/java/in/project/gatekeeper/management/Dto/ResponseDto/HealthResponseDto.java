package in.project.gatekeeper.management.Dto.ResponseDto;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@RequiredArgsConstructor
public class HealthResponseDto {

    private final String AppStatus;
    private final String DbStatus;
    private final LocalDateTime timestamp;

}
