package in.project.gatekeeper.management.Dto.ResponseDto;

import in.project.gatekeeper.management.Entities.RouterHeaderRule;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@AllArgsConstructor
public class HeaderRuleResponse {
    private Long id;
    private String direction;
    private String action;
    private String headerName;
    private String headerValue;

//    public HeaderRuleResponse(Long id, String direction, String action, String headerName, String headerValue) {
//
//    }

    public static HeaderRuleResponse from(RouterHeaderRule rule){
        return new HeaderRuleResponse(rule.getId(), rule.getDirection(),
                rule.getAction(), rule.getHeaderName(), rule.getHeaderValue());
    }
}
