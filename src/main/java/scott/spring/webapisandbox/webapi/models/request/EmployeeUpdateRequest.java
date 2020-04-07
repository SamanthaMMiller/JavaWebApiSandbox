package scott.spring.webapisandbox.webapi.models.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class EmployeeUpdateRequest {

    private String firstName;
    private String lastName;
}
