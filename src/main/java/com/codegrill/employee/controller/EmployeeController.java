package com.codegrill.employee.controller;

import com.codegrill.employee.model.Employee;
import com.codegrill.employee.repository.EmployeeRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import com.codegrill.employee.exception.ResourceNotFoundException;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@RestController
@RequestMapping("/api/v1/")
public class EmployeeController {

    private final EmployeeRepository employeeRepository;

    public EmployeeController(EmployeeRepository employeeRepository) {
        this.employeeRepository = employeeRepository;
    }


    // get employees
    @Operation(summary = "Get all employees", description = "Get the list of employees", tags = "Get")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Found the list of Employee",
                    content = {
                            @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = Employee.class)
                            )
                    }),
            @ApiResponse(
                    responseCode = "404",
                    description = "Employees not found",
                    content = @Content
            )
    })
    @GetMapping(value = "employees", produces = APPLICATION_JSON_VALUE)
    public List<Employee> getAllEmployee() {
        return this.employeeRepository.findAll();
    }

    //get employee by ID
    @Operation(summary = "Get single employee", description = "Get an employee by it's ID", tags = "Get")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Employee Found",
                    content = {
                            @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = Employee.class)
                            )
                    }),
            @ApiResponse(
                    responseCode = "404",
                    description = "Employee not found",
                    content = @Content
            )
    })
    @GetMapping("employees/{id}")
    public ResponseEntity<Employee> getEmployeeById(@PathVariable("id") Long employeeId) throws ResourceNotFoundException {
        Employee employee = employeeRepository.findById(employeeId)
                .orElseThrow(() -> new ResourceNotFoundException("Employee not found for this id :: " + employeeId));
        return ResponseEntity.ok().body(employee);
    }

    // save employee
    @PostMapping("employees")
    public Employee createEmployee(@RequestBody Employee employee) {
        return this.employeeRepository.save(employee);
    }

    // Update Employee
    @PutMapping("employees/{id}")
    public ResponseEntity<Employee> updateEmployee(@PathVariable("id") Long employeeId, @Validated @RequestBody Employee employeeDetails) throws ResourceNotFoundException {
        Employee employee = employeeRepository.findById(employeeId)
                .orElseThrow(() -> new ResourceNotFoundException("Employee not found for this id :: " + employeeId));

        if (!employeeDetails.getEmail().equals("")) employee.setEmail(employeeDetails.getEmail());
        if (!employeeDetails.getFirstName().equals("")) employee.setFirstName(employeeDetails.getFirstName());
        if (!employeeDetails.getLastName().equals("")) employee.setLastName(employeeDetails.getLastName());

        return ResponseEntity.ok(this.employeeRepository.save(employee));
    }

    // Delete Employee by ID
    @DeleteMapping("employees/{id}")
    public Map<String, Boolean> deleteEmployee(@PathVariable("id") Long employeeId) throws ResourceNotFoundException {
        Employee employee = employeeRepository.findById(employeeId)
                .orElseThrow(() -> new ResourceNotFoundException("Employee not found for this id :: " + employeeId));

        this.employeeRepository.delete(employee);

        Map<String, Boolean> response = new HashMap<>();
        response.put("deleted", Boolean.TRUE);
        return response;
    }
}
