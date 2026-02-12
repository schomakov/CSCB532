package com.nbu.CSCB532.model.logistics;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.Instant;

import static jakarta.persistence.CascadeType.MERGE;
import static jakarta.persistence.CascadeType.PERSIST;

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

    /** Получател – винаги чрез нормализиран контакт (име/телефон; опционално връзка с клиент). */
    @ManyToOne(optional = false)
    @JoinColumn(name = "recipient_contact_id")
    private RecipientContact recipientContact;

    /** Описание на съдържанието на пратката */
    @Column(length = 500)
    private String description;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private DeliveryType deliveryType;

    @ManyToOne
    @JoinColumn(name = "from_office_id")
    private Office fromOffice; // where registered/handed over

    @ManyToOne
    @JoinColumn(name = "to_office_id")
    private Office toOffice; // for TO_OFFICE deliveries

    /** Адрес за доставка (TO_ADDRESS): връзка към client_addresses. */
    @ManyToOne(cascade = {PERSIST, MERGE})
    @JoinColumn(name = "delivery_client_address_id")
    private ClientAddress deliveryClientAddress;

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

    /** Кой плаща: подател при изпращане или получател при доставка */
    @Enumerated(EnumType.STRING)
    @Column(name = "payment_type")
    private PaymentType paymentType;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ParcelStatus status;

    @Column(nullable = false, unique = true)
    private String trackingCode;

    @Column(nullable = false, updatable = false)
    private Instant createdAt;

    private Instant updatedAt;
    private Instant deliveredAt;

    /** Дата/час на плащане: при подател плаща = при регистрация; при получател плаща = при доставка/взимане от офис. */
    private Instant paidAt;

    @PrePersist
    public void prePersist() {
        Instant now = Instant.now();
        createdAt = now;
        updatedAt = now;
        if (status == null) {
            status = ParcelStatus.REGISTERED;
        }
        if (paymentType == PaymentType.SENDER_PAYS) {
            paidAt = now;
        }
    }

    @PreUpdate
    public void preUpdate() {
        updatedAt = Instant.now();
    }

    /** Удобство за показване: име на получател от контакта. */
    public String getRecipientName() {
        return recipientContact != null ? recipientContact.getName() : null;
    }

    /** Удобство за показване: телефон на получател от контакта. */
    public String getRecipientPhone() {
        return recipientContact != null ? recipientContact.getPhone() : null;
    }

    /** Удобство: получател като клиент (ако контактът е свързан с клиент). */
    public Client getRecipientClient() {
        return recipientContact != null ? recipientContact.getClient() : null;
    }

    /** Удобство за показване и валидации: адрес за доставка от client_address. */
    public Address getDeliveryAddress() {
        return deliveryClientAddress != null ? deliveryClientAddress.getAddress() : null;
    }
}

