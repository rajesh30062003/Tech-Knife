package com.techknife.backend.entity;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.CompoundIndexes;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
@Document(collection = "employees")
@CompoundIndexes({
    @CompoundIndex(name = "idx_emp_dept_status", def = "{'departmentId': 1, 'status': 1}"),
    @CompoundIndex(name = "idx_emp_name", def = "{'firstName': 1, 'lastName': 1}")
})
public class Employee extends BaseEntity {

    @NotBlank(message = "Employee ID is mandatory")
    @Indexed(unique = true)
    private String employeeId;

    @NotBlank(message = "Official email is mandatory")
    @Email(message = "Invalid official email format")
    @Indexed(unique = true)
    private String officialEmail;

    @Email(message = "Invalid personal email format")
    private String personalEmail;

    @NotBlank(message = "Primary mobile number is mandatory")
    @Size(min = 10, max = 15, message = "Primary mobile number must be between 10 and 15 digits")
    private String primaryMobile;

    private String alternateMobile;

    @NotBlank(message = "First name is mandatory")
    private String firstName;

    @NotBlank(message = "Last name is mandatory")
    private String lastName;

    private Gender gender;

    private LocalDate dob;

    private String bloodGroup;

    @Indexed
    private String departmentId;

    @Indexed
    private String designationId;

    @Indexed
    private String managerId;

    @NotNull(message = "Joining date is mandatory")
    private LocalDate joiningDate;

    @NotNull(message = "Employment type is mandatory")
    private EmploymentType employmentType;

    @PositiveOrZero(message = "Salary must be zero or positive")
    private BigDecimal salary;

    private List<String> skills = new ArrayList<>();

    private String githubUsername;

    private String profileImage;

    @NotNull(message = "Employee status is mandatory")
    @Indexed
    private EmployeeStatus status;

    public enum Gender {
        MALE,
        FEMALE,
        NON_BINARY,
        OTHER,
        PREFER_NOT_TO_SAY,
        UNSPECIFIED;

        @com.fasterxml.jackson.annotation.JsonCreator
        public static Gender fromString(String value) {
            if (value == null || value.isBlank()) {
                return UNSPECIFIED;
            }
            String normalized = value.trim().toUpperCase().replace(" ", "_").replace("-", "_");
            for (Gender g : values()) {
                if (g.name().equals(normalized)) {
                    return g;
                }
            }
            return UNSPECIFIED;
        }
    }

    public enum EmploymentType {
        FULL_TIME,
        PART_TIME,
        CONTRACT,
        INTERN,
        PROBATION,
        TEMPORARY,
        UNSPECIFIED;

        @com.fasterxml.jackson.annotation.JsonCreator
        public static EmploymentType fromString(String value) {
            if (value == null || value.isBlank()) {
                return UNSPECIFIED;
            }
            String normalized = value.trim().toUpperCase().replace(" ", "_").replace("-", "_");
            for (EmploymentType et : values()) {
                if (et.name().equals(normalized)) {
                    return et;
                }
            }
            return UNSPECIFIED;
        }
    }

    public enum EmployeeStatus {
        ACTIVE,
        INACTIVE,
        ON_LEAVE,
        PROBATION,
        NOTICE_PERIOD,
        TERMINATED,
        RESIGNED,
        UNSPECIFIED;

        @com.fasterxml.jackson.annotation.JsonCreator
        public static EmployeeStatus fromString(String value) {
            if (value == null || value.isBlank()) {
                return UNSPECIFIED;
            }
            String normalized = value.trim().toUpperCase().replace(" ", "_").replace("-", "_");
            for (EmployeeStatus es : values()) {
                if (es.name().equals(normalized)) {
                    return es;
                }
            }
            return UNSPECIFIED;
        }
    }
}
