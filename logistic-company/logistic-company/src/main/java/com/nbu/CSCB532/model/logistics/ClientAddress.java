package com.nbu.CSCB532.model.logistics;

import jakarta.persistence.*;
import lombok.*;

import static jakarta.persistence.CascadeType.MERGE;
import static jakarta.persistence.CascadeType.PERSIST;

/**
 * Клиент ↔ много адреси. Използва се за адрес на доставка в parcels (delivery_client_address_id).
 * Ако client_id е null, адресът е еднократен (само за конкретна пратка).
 */
@Entity
@Table(name = "client_addresses")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ClientAddress {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "client_id")
    private Client client;

    @ManyToOne(optional = false, cascade = {PERSIST, MERGE})
    @JoinColumn(name = "address_id")
    private Address address;

    @Column(length = 100)
    private String label; // напр. "Дом", "Офис"
}
