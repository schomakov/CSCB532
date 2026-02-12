package com.nbu.CSCB532.model.logistics;

import jakarta.persistence.*;
import lombok.*;

/**
 * Нормализирана таблица за адреси – използва се от компании, офиси, клиенти и пратки
 * чрез външни ключове вместо повторение на колони.
 */
@Entity
@Table(name = "addresses")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Address {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String country;
    private String city;

    @Column(name = "zip_code")
    private String zipCode;

    private String street;
    private String details; // entrance, floor, apartment, notes
}
