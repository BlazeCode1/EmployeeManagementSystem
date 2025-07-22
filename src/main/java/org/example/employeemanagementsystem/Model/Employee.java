package org.example.employeemanagementsystem.Model;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor

public class Employee {

    @NotEmpty(message = "ID Cannot Be Empty")
    @Size(min = 2,message = "ID length should be 2 or more")
    private String ID;

    @NotEmpty(message = "Name Cannot Be Empty!")
    @Size(min = 4,message = "Name length should be 4 or more")
    @Pattern(regexp = "^[a-zA-Z]*$", message = "Field must contain only characters (no numbers).")
    private String name;

    @Email(message = "Must Be A Valid Email Format")
    private String email;

    @NotEmpty(message = "Phone Number Cannot Be Empty")
    @Pattern(regexp = "^05\\d{8}$", message = "Phone number must start with 05 and be 10 digits long")
    private String phoneNumber;
    @NotNull
    @Min(value = 26)
    @Positive(message = "Must Be A Number")
    private int age;

    @NotEmpty(message = "position Cannot Be Empty")
    @Pattern(regexp = "^(supervisor|coordinator)$", message = "Position can only be Supervisor or coordinator")
    private String position;

    @NotNull(message = "On leave Cannot Be Empty Set On Leave to False.")
    private boolean onLeave;


    @NotNull(message = "Hire Date Cannot Be Null")
    @FutureOrPresent(message = "Hire date should be present or in the future")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime hireDate;
    @NotNull(message = "Annual leave Cannot Be Null")
    @Positive(message = "Only Positive Number")
    private int annualLeave;

}
