package com.nbu.CSCB532.model.logistics;

import com.nbu.CSCB532.model.auth.User;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "employees")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Employee {
    @Id
    private Long id; // same as User.id

    @OneToOne
    @MapsId
    @JoinColumn(name = "id")
    private User user;

    @ManyToOne(optional = false)
    @JoinColumn(name = "company_id")
    private Company company;

    @ManyToOne
    @JoinColumn(name = "office_id")
    private Office office; // assigned office (optional)

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private EmployeeType type; // COURIER or OFFICE_EMPLOYEE

    private String phone;
    private Boolean active;
}

