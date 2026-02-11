package com.nbu.CSCB532.model.logistics;

import jakarta.persistence.*;
import lombok.*;
import java.time.Instant;

@Entity
@Table(name = "companies")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Company {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String name;

    @Column(nullable = true, unique = true)
    private String registrationNumber; // BULSTAT or equivalent

    @Embedded
    @AttributeOverrides({
            @AttributeOverride(name = "country", column = @Column(name = "headquarters_address_country")),
            @AttributeOverride(name = "city", column = @Column(name = "headquarters_address_city")),
            @AttributeOverride(name = "zipCode", column = @Column(name = "headquarters_address_zip_code")),
            @AttributeOverride(name = "street", column = @Column(name = "headquarters_address_street")),
            @AttributeOverride(name = "details", column = @Column(name = "headquarters_address_details"))
    })
    private Address headquartersAddress;

    private String description;

    @Column(nullable = false, updatable = false)
    private Instant createdAt;

    private Instant updatedAt;

    @PrePersist
    public void prePersist() {
        createdAt = Instant.now();
        updatedAt = createdAt;
    }

    @PreUpdate
    public void preUpdate() {
        updatedAt = Instant.now();
    }
}

