package com.nbu.CSCB532.model.logistics;

import jakarta.persistence.*;
import lombok.*;

/**
 * Нормализиран получател: име и телефон. Ако client_id е зададен, получателят е регистриран клиент.
 * В parcels се пази само recipient_contact_id – без свободен текст.
 */
@Entity
@Table(name = "recipient_contacts")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RecipientContact {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 255)
    private String name;

    @Column(length = 50)
    private String phone;

    /** Ако е зададен, получателят е регистриран клиент (данните могат да се вземат и от User). */
    @ManyToOne
    @JoinColumn(name = "client_id")
    private Client client;
}
