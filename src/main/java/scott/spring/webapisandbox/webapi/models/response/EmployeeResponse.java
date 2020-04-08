package scott.spring.webapisandbox.webapi.models.response;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import scott.spring.webapisandbox.models.Employee;

@Getter
@Setter
@AllArgsConstructor
@ApiModel(description="Details of a retrieved employee record.")
public class EmployeeResponse {

    @ApiModelProperty(notes = "The employee id")
    private Integer id;

    @ApiModelProperty(notes = "The employee first name")
    private String firstName;

    @ApiModelProperty(notes = "The employee last name")
    private String lastName;

    public static EmployeeResponse FromEmployee(Employee employee) {
        return new EmployeeResponse(
                employee.getId(),
                employee.getFirstName(),
                employee.getLastName()
        );
    }
}
