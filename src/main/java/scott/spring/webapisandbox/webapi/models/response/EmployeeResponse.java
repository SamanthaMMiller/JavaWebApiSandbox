package scott.spring.webapisandbox.webapi.models.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import scott.spring.webapisandbox.models.Employee;

@Getter
@Setter
@AllArgsConstructor
public class EmployeeResponse {

    private Integer id;
    private String firstName;
    private String lastName;

    public static EmployeeResponse FromEmployee(Employee employee) {
        return new EmployeeResponse(
                employee.getId(),
                employee.getFirstName(),
                employee.getLastName()
        );
    }
}
