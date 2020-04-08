package scott.spring.webapisandbox.webapi.controllers;

import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import scott.spring.webapisandbox.exceptions.EmployeeNotFoundException;
import scott.spring.webapisandbox.models.Employee;
import scott.spring.webapisandbox.repositories.EmployeeRepository;
import scott.spring.webapisandbox.webapi.models.request.EmployeeCreateRequest;
import scott.spring.webapisandbox.webapi.models.request.EmployeeUpdateRequest;
import scott.spring.webapisandbox.webapi.models.response.EmployeeResponse;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.util.List;
import java.util.stream.Collectors;

@RestController()
@RequestMapping("/api/employee")
public class EmployeeController
{
	private final EmployeeRepository _employeeRepository;

	public EmployeeController(EmployeeRepository employeeRepository) {
		this._employeeRepository = employeeRepository;
	}

	@GetMapping("")
	@ApiOperation(value = "Gets a list of available employees", response = List.class)
	@ApiResponses(value = {
		@ApiResponse(code = 200, message = "Successfully retrieved list of employees"),
		@ApiResponse(code = 401, message = "You are not authorized to view the resource"),
		@ApiResponse(code = 403, message = "Accessing the resource you were trying to reach is forbidden"),
		@ApiResponse(code = 404, message = "The resource you were trying to reach is not found")
	})
	public ResponseEntity<List<EmployeeResponse>> getAll() {

		List<Employee> employees = _employeeRepository.findAll();

		List<EmployeeResponse> response = employees
			.stream()
			.map(EmployeeResponse::FromEmployee)
			.map(EmployeeResponse.class::cast)
			.collect(Collectors.toList());

		return ResponseEntity.ok().body(response);
	}

	@GetMapping("{id}")
	@ApiOperation(value = "Gets an employee by Id")
	@ApiResponses(value = {
		@ApiResponse(code = 200, message = "Successfully retrieved employee"),
		@ApiResponse(code = 401, message = "You are not authorized to view the resource"),
		@ApiResponse(code = 403, message = "Accessing the resource you were trying to reach is forbidden"),
		@ApiResponse(code = 404, message = "The resource you were trying to reach is not found")
	})
	public ResponseEntity<EmployeeResponse> getById(
		@ApiParam(value = "Id of the employee to retrieve.", required = true) @PathVariable(value = "id")
		Integer employeeId
	) throws EmployeeNotFoundException {
		if (employeeId <= 0) {
			return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
		}

		Employee employee = _employeeRepository
				.findById(employeeId)
				.orElseThrow(() -> new EmployeeNotFoundException("No employee was found with id " + employeeId));

		EmployeeResponse response = EmployeeResponse.FromEmployee(employee);
		return ResponseEntity.ok().body(response);
	}

	@ApiOperation(value = "Creates a new employee.")
	@PostMapping("")
	@ApiResponses(value = {
		@ApiResponse(code = 200, message = "Successfully created employee"),
		@ApiResponse(code = 401, message = "You are not authorized to view the resource"),
		@ApiResponse(code = 403, message = "Accessing the resource you were trying to reach is forbidden"),
		@ApiResponse(code = 404, message = "The resource you were trying to reach is not found")
	})
	public ResponseEntity<EmployeeResponse> create(
		@ApiParam(value = "Details of the employee to create", required = true) @NotNull @Valid @RequestBody
			EmployeeCreateRequest employeeRequest
	) {
		Employee employee = new Employee(
				null,
				employeeRequest.getFirstName(),
				employeeRequest.getLastName()
		);
		employee =  _employeeRepository.save(employee);

		EmployeeResponse response = EmployeeResponse.FromEmployee(employee);
		return ResponseEntity.ok().body(response);
	}

	@ApiOperation(value = "Updates an existing employee.")
	@PutMapping("{id}")
	@ApiResponses(value = {
		@ApiResponse(code = 200, message = "Successfully updated employee"),
		@ApiResponse(code = 401, message = "You are not authorized to view the resource"),
		@ApiResponse(code = 403, message = "Accessing the resource you were trying to reach is forbidden"),
		@ApiResponse(code = 404, message = "The resource you were trying to reach is not found")
	})
	public ResponseEntity<EmployeeResponse> update(
			@ApiParam(value = "Id of the employee to update.", required = true) @PathVariable(value = "id")
			Integer employeeId,
			@ApiParam(value = "Update employee object", required = true) @NotNull @Valid @RequestBody
				EmployeeUpdateRequest employeeRequest
	) throws EmployeeNotFoundException {

		Employee employee = _employeeRepository
				.findById(employeeId)
				.orElseThrow(() -> new EmployeeNotFoundException("No employee was found with id " + employeeId));

		employee.setLastName(employeeRequest.getLastName());
		employee.setFirstName(employeeRequest.getFirstName());

		final Employee updatedEmployee = _employeeRepository.save(employee);

		EmployeeResponse response = EmployeeResponse.FromEmployee(updatedEmployee);

		return ResponseEntity.ok(response);
	}
}
