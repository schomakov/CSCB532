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
    @AttributeOverrides({
            @AttributeOverride(name = "country", column = @Column(name = "address_country")),
            @AttributeOverride(name = "city", column = @Column(name = "address_city")),
            @AttributeOverride(name = "zipCode", column = @Column(name = "address_zip_code")),
            @AttributeOverride(name = "street", column = @Column(name = "address_street")),
            @AttributeOverride(name = "details", column = @Column(name = "address_details"))
    })
    private Address address;

    private String phone;
    private String email;
    @Column(name = "working_hours")
    private String workingHours;
    private Boolean active;
}

