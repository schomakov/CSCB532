package com.nbu.CSCB532.model.logistics;

import jakarta.persistence.*;
import lombok.*;

import static jakarta.persistence.CascadeType.MERGE;
import static jakarta.persistence.CascadeType.PERSIST;

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

    @Column(nullable = false)
    private String name;

    @ManyToOne(optional = false, cascade = {PERSIST, MERGE})
    @JoinColumn(name = "address_id")
    private Address address;

    private String phone;
    private String email;
    @Column(name = "working_hours")
    private String workingHours;
    private Boolean active;
}

