package scott.spring.webapisandbox.controllers;

import org.springframework.http.MediaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultActions;
import scott.spring.webapisandbox.models.Employee;
import scott.spring.webapisandbox.repositories.EmployeeRepository;
import scott.spring.webapisandbox.webapi.controllers.EmployeeController;
import scott.spring.webapisandbox.webapi.models.request.EmployeeCreateRequest;
import scott.spring.webapisandbox.webapi.models.request.EmployeeUpdateRequest;

import java.util.ArrayList;
import java.util.Optional;

import static java.util.Optional.of;
import static org.hamcrest.Matchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

@WebMvcTest(EmployeeController.class)
public class EmployeeControllerTests {

    @Autowired
    private MockMvc _mockMvc;

    @Autowired
    private ObjectMapper _objectMapper;

    @MockBean
    private EmployeeRepository _mockEmployeeRepository;

    @Test
    public void given_NoEmployees_Then_GetAll_Returns_EmptyList() throws Exception {
        // Arrange
        when(_mockEmployeeRepository.findAll()).thenReturn(new ArrayList<>());

        // Act / Assert
        this._mockMvc
            .perform(get("/api/employee"))
            .andDo(print())
            .andExpect(status().isOk())
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$", hasSize(0)));
    }

    @Test
    public void given_OneEmployee_Then_GetAll_Returns_ListWithOneEmployee() throws Exception {
        // Arrange
        Employee mockEmployee = new Employee(1, "TestFirst", "TestLast");
        when(_mockEmployeeRepository.findAll()).thenReturn(new ArrayList<>() {{
            add(mockEmployee);
        }});

        // Act
        ResultActions resultActions = this._mockMvc
            .perform(get("/api/employee"))
            .andDo(print())
            .andExpect(status().isOk());
        MvcResult mvcResult = resultActions.andReturn();

        // Assert
        String resultAsString = mvcResult.getResponse().getContentAsString();
        Employee[] resultEmployees = _objectMapper.readValue(resultAsString, Employee[].class);

        Assertions.assertNotNull(resultEmployees);
        Assertions.assertEquals(1, resultEmployees.length);
        assertEmployeeResult(mockEmployee, resultEmployees[0]);
    }

    @Test
    public void given_TwoEmployees_Then_GetAll_Returns_ListWithTwoEmployees() throws Exception {
        // Arrange
        Employee mockEmployee1 = new Employee(1, "TestFirst1", "TestLast1");
        Employee mockEmployee2 = new Employee(2, "TestFirst2", "TestLast2");
        when(_mockEmployeeRepository.findAll()).thenReturn(new ArrayList<>() {{
            add(mockEmployee1);
            add(mockEmployee2);
        }});

        // Act
        ResultActions resultActions = this._mockMvc
            .perform(get("/api/employee"))
            .andDo(print())
            .andExpect(status().isOk());
        MvcResult mvcResult = resultActions.andReturn();

        // Assert
        String resultAsString = mvcResult.getResponse().getContentAsString();
        Employee[] resultEmployees = _objectMapper.readValue(resultAsString, Employee[].class);

        Assertions.assertNotNull(resultEmployees);
        Assertions.assertEquals(2, resultEmployees.length);
        assertEmployeeResult(mockEmployee1, resultEmployees[0]);
        assertEmployeeResult(mockEmployee2, resultEmployees[1]);
    }

    @Test
    public void given_EmployeeExistsForSpecifiedId_Then_GetById_Returns_Employee() throws Exception {
        // Arrange
        Employee mockEmployee1 = new Employee(1, "TestFirst1", "TestLast1");
        when(_mockEmployeeRepository.findById(Mockito.isA(Integer.class))).thenReturn(of(mockEmployee1));

        // Act
        ResultActions resultActions = this._mockMvc
            .perform(get(String.format("/api/employee/%d", 1)))
            .andDo(print())
            .andExpect(status().isOk());
        MvcResult mvcResult = resultActions.andReturn();

        // Assert
        String resultAsString = mvcResult.getResponse().getContentAsString();
        Employee resultEmployee = _objectMapper.readValue(resultAsString, Employee.class);

        Assertions.assertNotNull(resultEmployee);
        assertEmployeeResult(mockEmployee1, resultEmployee);
    }

    @Test
    public void given_EmployeeDoesNotExistForSpecifiedId_Then_GetById_Returns_NotFoundResult() throws Exception {
        // Arrange
        when(_mockEmployeeRepository.findById(Mockito.isA(Integer.class))).thenReturn(Optional.empty());

        // Act / Assert
        this._mockMvc
            .perform(get(String.format("/api/employee/%d", 1)))
            .andDo(print())
            .andExpect(status().isNotFound());
    }

    @Test
    public void given_SpecifiedIdIsZero_Then_GetById_Returns_BadRequestResult() throws Exception {
        // Act / Assert
        this._mockMvc
            .perform(get(String.format("/api/employee/%d", 0)))
            .andDo(print())
            .andExpect(status().isBadRequest());
    }

    @Test
    public void given_SpecifiedIdLessThanZero_Then_GetById_Returns_BadRequestResult() throws Exception {
        // Act / Assert
        this._mockMvc
            .perform(get(String.format("/api/employee/%d", -1)))
            .andDo(print())
            .andExpect(status().isBadRequest());
    }

    @Test
    public void given_FirstAndLastNameProvided_Then_Create_AddsNewEmployee_And_ReturnsEmployee() throws Exception {
        // Arrange
        Employee mockEmployee1 = new Employee(1, "TestFirst1", "TestLast1");
        when(_mockEmployeeRepository.save(Mockito.isA(Employee.class))).thenReturn(mockEmployee1);
        EmployeeCreateRequest employeeCreateRequest = new EmployeeCreateRequest(mockEmployee1.getFirstName(), mockEmployee1.getFirstName());

        // Act
        ResultActions resultActions = this._mockMvc
            .perform(post("/api/employee")
                .content(_objectMapper.writeValueAsString(employeeCreateRequest))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
            .andDo(print())
            .andExpect(status().isOk());
        MvcResult mvcResult = resultActions.andReturn();

        // Assert
        String resultAsString = mvcResult.getResponse().getContentAsString();
        Employee resultEmployee = _objectMapper.readValue(resultAsString, Employee.class);

        Assertions.assertNotNull(resultEmployee);
        assertEmployeeResult(mockEmployee1, resultEmployee);
    }

    @Test
    public void given_RequestNotProvided_Then_Create_Returns_BadRequestResult() throws Exception {
        // Arrange
        Employee mockEmployee1 = new Employee(1, "TestFirst1", null);
        when(_mockEmployeeRepository.save(Mockito.isA(Employee.class))).thenReturn(mockEmployee1);

        // Act / Assert
        this._mockMvc
            .perform(post("/api/employee")
                .content(_objectMapper.writeValueAsString(null))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
            .andDo(print())
            .andExpect(status().isBadRequest());
    }

    @Test
    public void given_FirstNameNotProvided_Then_Create_Returns_BadRequestResult() throws Exception {
        // Arrange
        Employee mockEmployee1 = new Employee(1, "TestFirst1", null);
        when(_mockEmployeeRepository.save(Mockito.isA(Employee.class))).thenReturn(mockEmployee1);
        EmployeeCreateRequest employeeCreateRequest = new EmployeeCreateRequest(null, mockEmployee1.getFirstName());

        // Act / Assert
        this._mockMvc
            .perform(post("/api/employee")
                .content(_objectMapper.writeValueAsString(employeeCreateRequest))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
            .andDo(print())
            .andExpect(status().isBadRequest());
    }

    @Test
    public void given_LastNameNotProvided_Then_Create_Returns_BadRequestResult() throws Exception {
        // Arrange
        Employee mockEmployee1 = new Employee(1, null, "TestLast1");
        when(_mockEmployeeRepository.save(Mockito.isA(Employee.class))).thenReturn(mockEmployee1);
        EmployeeCreateRequest employeeCreateRequest = new EmployeeCreateRequest(mockEmployee1.getFirstName(), null);

        // Act / Assert
        this._mockMvc
            .perform(post("/api/employee")
                .content(_objectMapper.writeValueAsString(employeeCreateRequest))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
            .andDo(print())
            .andExpect(status().isBadRequest());
    }

    @Test
    public void given_EmployeeExistsForSpecifiedId_And_FirstAndLastNameProvided_Then_Update_UpdatesEmployee_And_ReturnsEmployee() throws Exception {
        // Arrange
        Employee mockEmployee1Initial = new Employee(1, "TestFirst1", "TestLast1");
        Employee mockEmployee1Updated = new Employee(1, "TestFirst1Updated", "TestLast1Updated");
        when(_mockEmployeeRepository.findById(Mockito.isA(Integer.class))).thenReturn(of(mockEmployee1Initial));
        when(_mockEmployeeRepository.save(Mockito.isA(Employee.class))).thenReturn(mockEmployee1Updated);
        EmployeeUpdateRequest employeeUpdateRequest = new EmployeeUpdateRequest(mockEmployee1Updated.getFirstName(), mockEmployee1Updated.getFirstName());

        // Act
        ResultActions resultActions = this._mockMvc
            .perform(put(String.format("/api/employee/%d", 1))
                .content(_objectMapper.writeValueAsString(employeeUpdateRequest))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
            .andDo(print())
            .andExpect(status().isOk());
        MvcResult mvcResult = resultActions.andReturn();

        // Assert
        String resultAsString = mvcResult.getResponse().getContentAsString();
        Employee resultEmployee = _objectMapper.readValue(resultAsString, Employee.class);

        Assertions.assertNotNull(resultEmployee);
        assertEmployeeResult(mockEmployee1Updated, resultEmployee);
    }

    @Test
    public void given_EmployeeDoesNotExistForSpecifiedId_Then_Update_ReturnsNotFoundResult() throws Exception {
        // Arrange
        when(_mockEmployeeRepository.findById(Mockito.isA(Integer.class))).thenReturn(Optional.empty());
        EmployeeUpdateRequest employeeUpdateRequest = new EmployeeUpdateRequest("TestFirst1Updated", "TestLast1Updated");

        // Act / Assert
        this._mockMvc
            .perform(put(String.format("/api/employee/%d", 2))
                .content(_objectMapper.writeValueAsString(employeeUpdateRequest))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
            .andDo(print())
            .andExpect(status().isNotFound());
    }

    @Test
    public void given_EmployeeExistsForSpecifiedId_And_RequestNotProvided_Then_Update_ReturnsBadRequestResult() throws Exception {
        // Arrange
        Employee mockEmployee1Initial = new Employee(1, "TestFirst1", "TestLast1");
        when(_mockEmployeeRepository.findById(Mockito.isA(Integer.class))).thenReturn(of(mockEmployee1Initial));

        // Act / Assert
        this._mockMvc
            .perform(put(String.format("/api/employee/%d", 1))
                .content(_objectMapper.writeValueAsString(null))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
            .andDo(print())
            .andExpect(status().isBadRequest());
    }

    @Test
    public void given_EmployeeExistsForSpecifiedId_And_FirstNameNotProvided_Then_Update_ReturnsBadRequestResult() throws Exception {
        // Arrange
        Employee mockEmployee1Initial = new Employee(1, "TestFirst1", "TestLast1");
        when(_mockEmployeeRepository.findById(Mockito.isA(Integer.class))).thenReturn(of(mockEmployee1Initial));
        EmployeeUpdateRequest employeeUpdateRequest = new EmployeeUpdateRequest(null, "TestLast1Updated");

        // Act / Assert
        this._mockMvc
            .perform(put(String.format("/api/employee/%d", 1))
                .content(_objectMapper.writeValueAsString(employeeUpdateRequest))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
            .andDo(print())
            .andExpect(status().isBadRequest());
    }

    @Test
    public void given_EmployeeExistsForSpecifiedId_And_LastNameNotProvided_The_Update_ReturnsBadRequestResult() throws Exception {
        // Arrange
        Employee mockEmployee1Initial = new Employee(1, "TestFirst1", "TestLast1");
        when(_mockEmployeeRepository.findById(Mockito.isA(Integer.class))).thenReturn(of(mockEmployee1Initial));
        EmployeeUpdateRequest employeeUpdateRequest = new EmployeeUpdateRequest("TestFirst1Updated", null);

        // Act / Assert
        new EmployeeCreateRequest("TestFirst", "TestLast");
        this._mockMvc
            .perform(put(String.format("/api/employee/%d", 1))
                .content(_objectMapper.writeValueAsString(employeeUpdateRequest))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
            .andDo(print())
            .andExpect(status().isBadRequest());
    }

    private void assertEmployeeResult(Employee expected, Employee actual) {
        Assertions.assertNotNull(actual);
        Assertions.assertEquals(expected.getId(), actual.getId());
        Assertions.assertEquals(expected.getFirstName(), actual.getFirstName());
        Assertions.assertEquals(expected.getLastName(), actual.getLastName());
    }
}
