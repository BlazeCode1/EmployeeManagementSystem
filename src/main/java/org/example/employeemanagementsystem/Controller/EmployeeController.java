package org.example.employeemanagementsystem.Controller;

import jakarta.validation.Valid;
import org.example.employeemanagementsystem.Api.ApiResponse;
import org.example.employeemanagementsystem.Model.Employee;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;

@RestController
@RequestMapping("/api/v1/employee")
public class EmployeeController {

    ArrayList<Employee> employees = new ArrayList<>();


    @GetMapping("/get")
    public ResponseEntity<?> getEmployees() {
        return ResponseEntity.ok(employees);
    }

    @PostMapping("/add")
    public ResponseEntity<?> addEmployee(@Valid @RequestBody Employee employee, Errors err) {
        if (err.hasErrors()) {
            String error = err.getFieldError().getDefaultMessage();
            return ResponseEntity.badRequest().body(new ApiResponse(error));
        }
        if (employee.isOnLeave()) {
            return ResponseEntity.badRequest().body(new ApiResponse("onLeave Must Be Set To False"));
        }
        for (Employee e : employees) {
            if (employee.getID().equals(e.getID())) {
                return ResponseEntity.badRequest().body(new ApiResponse("Employee With Given ID Already Exist."));
            }
        }

        employees.add(employee);
        return ResponseEntity.ok(new ApiResponse("Employee Added Successfully"));
    }


    @PutMapping("/update")
    public ResponseEntity<?> updateEmployee(@Valid @RequestBody Employee employee, Errors err) {
        if (err.hasErrors()) {
            String error = err.getFieldError().getDefaultMessage();
            return ResponseEntity.badRequest().body(new ApiResponse(error));
        }
        for (Employee e : employees) {
            if (e.getID().equals(employee.getID())) {
                employees.set(employees.indexOf(e), employee);
                return ResponseEntity.ok(new ApiResponse("Employee Updated Successfully."));
            }
        }

        return ResponseEntity.badRequest().body(new ApiResponse("Employee With Given Id Not Found."));
    }


    @DeleteMapping("/delete/{ID}")
    public ResponseEntity<?> deleteEmployee(@PathVariable String ID) {
        for (Employee e : employees) {
            if (ID.equals(e.getID())) {
                employees.remove(e);
                return ResponseEntity.ok(new ApiResponse("Employee Exist and Removed Successfully!"));
            }
        }
        return ResponseEntity.badRequest().body(new ApiResponse("Employee With Given ID Not Found"));
    }

    @GetMapping("/filter/{position}")
    public ResponseEntity<?> searchByPosition(@PathVariable String position) {
        String pos = position.trim().toLowerCase();
        switch (pos) {
            case "coordinator", "supervisor":
                ArrayList<Employee> filteredEmployees = new ArrayList<>();
                for (Employee e : employees) {
                    if (e.getPosition().equalsIgnoreCase(pos)) {
                        filteredEmployees.add(e);
                    }
                }
                return ResponseEntity.ok(filteredEmployees);

            default:
                return ResponseEntity.badRequest().body(new ApiResponse("Position Can Only Be Coordinator or Supervisor"));
        }
    }


    @GetMapping("/filter/age/{min}/{max}")
    public ResponseEntity<?> getEmployeeByAgeRange(@PathVariable int min, @PathVariable int max) {

        if (max <= min) {
            return ResponseEntity.badRequest().body(new ApiResponse("max should be bigger than min,/age/{min}/{max}"));
        }
        if (min < 26) {
            return ResponseEntity.badRequest().body(new ApiResponse("min should be bigger than 25"));
        }
        ArrayList<Employee> filteredEmployees = new ArrayList<>();
        for (Employee e : employees) {
            if (min > e.getAge() || e.getAge() < max) {
                filteredEmployees.add(e);
            }

        }
        return ResponseEntity.ok(filteredEmployees);
    }

    @PostMapping("/annual/apply/{ID}")
    public ResponseEntity<?> applyForAnnualLeave(@PathVariable String ID) {

        for (Employee e : employees) {
            if (ID.equals(e.getID())) {
                if (e.isOnLeave()) {
                    return ResponseEntity.badRequest().body(new ApiResponse("Employee is on leave"));
                } else {
                    if (e.getAnnualLeave() > 0) {
                        e.setOnLeave(true);
                        e.setAnnualLeave(e.getAnnualLeave() - 1);
                        return ResponseEntity.ok(new ApiResponse("Annual Leave Applied for: " + e.getName()));
                    } else {
                        return ResponseEntity.badRequest().body(new ApiResponse("Employee Annual Credit Insufficient"));
                    }
                }
            }
        }
        return ResponseEntity.badRequest().body(new ApiResponse("Employee With Given ID Not Found"));
    }


    @GetMapping("/annual/get/no-annual-leave")
    public ResponseEntity<?> employeesWithNoAnnualLeave() {
        ArrayList<Employee> filteredEmployee = new ArrayList<>();
        for (Employee e : employees) {
            if (e.getAnnualLeave() == 0) {
                filteredEmployee.add(e);
            }

        }
        if (filteredEmployee.isEmpty()) {
            return ResponseEntity.badRequest().body(new ApiResponse("All Employees Have Annual Leaves"));
        }
        return ResponseEntity.ok(filteredEmployee);
    }


    @PutMapping("/promote/{superID}/{promotedID}")
    public ResponseEntity<?> promoteEmployee(@PathVariable String superID, @PathVariable String promotedID) {
        boolean superflag = false;

        for (Employee e : employees) {
            if (e.getID().equals(superID)) {
                superflag = true;
                break;
            }
        }

        if (!superflag) {
            return ResponseEntity.badRequest().body(new ApiResponse("Supervisor ID is not correct"));
        }

        for (Employee e : employees) {
            if (e.getID().equals(promotedID)) {
                if (e.getAge() <= 30) {
                    return ResponseEntity.badRequest().body(new ApiResponse("Employee must be older than 30"));
                }

                if (e.isOnLeave()) {
                    return ResponseEntity.badRequest().body(new ApiResponse("Employee currently on leave"));
                }

                if (e.getPosition().equalsIgnoreCase("supervisor")) {
                    return ResponseEntity.badRequest().body(new ApiResponse("Employee is already a supervisor"));
                }

                e.setPosition("supervisor");
                return ResponseEntity.ok(new ApiResponse("Employee promoted successfully"));
            }
        }

        return ResponseEntity.badRequest().body(new ApiResponse("Promoted employee ID not found"));
    }


}
