package com.nbu.CSCB532.model.logistics;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "offices")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Office {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "company_id")
    private Company company;

    @Column(nullable = false)
    private String name;

    @Embedded
    private Address address;

    private String phone;
    private String email;
    private String workingHours;
    private Boolean active;
}

