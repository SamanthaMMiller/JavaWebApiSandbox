package scott.spring.webapisandbox.webapi.models.request;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotBlank;

@Getter
@Setter
@AllArgsConstructor
@ApiModel(description="New details for an existing employee to be updated.")
public class EmployeeUpdateRequest {

    @ApiModelProperty(notes = "The employee first name")
    @NotBlank(message = "The first name must be specified")
    private String firstName;

    @ApiModelProperty(notes = "The employee last name")
    @NotBlank(message = "The last name must be specified")
    private String lastName;
}
