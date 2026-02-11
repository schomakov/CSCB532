package com.nbu.CSCB532.model.logistics;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.Instant;

@Entity
@Table(name = "parcels", indexes = {
        @Index(name = "ux_tracking_code", columnList = "trackingCode", unique = true)
})
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Parcel {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "sender_id")
    private Client sender;

    private String recipientName;
    private String recipientPhone;

    @ManyToOne
    @JoinColumn(name = "recipient_client_id")
    private Client recipientClient; // optional, when recipient is a registered client

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private DeliveryType deliveryType;

    @ManyToOne
    @JoinColumn(name = "from_office_id")
    private Office fromOffice; // where registered/handed over

    @ManyToOne
    @JoinColumn(name = "to_office_id")
    private Office toOffice; // for TO_OFFICE deliveries

    @Embedded
    @AttributeOverrides({
            @AttributeOverride(name = "country", column = @Column(name = "delivery_address_country")),
            @AttributeOverride(name = "city", column = @Column(name = "delivery_address_city")),
            @AttributeOverride(name = "zipCode", column = @Column(name = "delivery_address_zip_code")),
            @AttributeOverride(name = "street", column = @Column(name = "delivery_address_street")),
            @AttributeOverride(name = "details", column = @Column(name = "delivery_address_details"))
    })
    private Address deliveryAddress; // for TO_ADDRESS deliveries

    @ManyToOne
    @JoinColumn(name = "courier_id")
    private Employee courier; // optional

    @ManyToOne
    @JoinColumn(name = "registered_by_employee_id")
    private Employee registeredBy; // employee who registered the parcel

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal weightKg;

    @Column(precision = 12, scale = 2)
    private BigDecimal price;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ParcelStatus status;

    @Column(nullable = false, unique = true)
    private String trackingCode;

    @Column(nullable = false, updatable = false)
    private Instant createdAt;

    private Instant updatedAt;
    private Instant deliveredAt;

    @PrePersist
    public void prePersist() {
        createdAt = Instant.now();
        updatedAt = createdAt;
        if (status == null) {
            status = ParcelStatus.REGISTERED;
        }
    }

    @PreUpdate
    public void preUpdate() {
        updatedAt = Instant.now();
    }
}

