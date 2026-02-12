package com.nbu.CSCB532.service.logistics;

import com.nbu.CSCB532.model.logistics.DeliveryType;
import com.nbu.CSCB532.model.logistics.Parcel;
import com.nbu.CSCB532.model.logistics.ParcelStatus;
import com.nbu.CSCB532.model.logistics.PaymentType;
import com.nbu.CSCB532.repository.logistics.ParcelRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Сървиз за управление на пратки: CRUD, търсене по критерии и изчисляване на цена.
 * Цената се определя от тегло (базова такса + лв/кг) и тип доставка (до офис по-евтино, до адрес с доплащане).
 */
@Service
@RequiredArgsConstructor
public class ParcelService {
    private final ParcelRepository parcelRepository;

    /** Базова такса до офис (лв.) */
    private static final BigDecimal BASE_FEE_TO_OFFICE = new BigDecimal("5.00");
    /** Базова такса до адрес (лв.) */
    private static final BigDecimal BASE_FEE_TO_ADDRESS = new BigDecimal("7.50");
    private static final BigDecimal PER_KG = new BigDecimal("4.00");

    public List<Parcel> findAll() {
        return parcelRepository.findAll();
    }

    /** Филтриране по име на получател и/или име на подател. Празни стойности = без филтър. */
    public List<Parcel> findFiltered(String recipientName, String senderName) {
        boolean hasRecipient = recipientName != null && !recipientName.isBlank();
        boolean hasSender = senderName != null && !senderName.isBlank();
        if (!hasRecipient && !hasSender) return parcelRepository.findAll();
        if (hasRecipient && !hasSender) return parcelRepository.findByRecipientNameContainingIgnoreCase(recipientName.trim());
        if (!hasRecipient && hasSender) return parcelRepository.findBySenderNameContainingIgnoreCase(senderName.trim());
        List<Parcel> byRecipient = parcelRepository.findByRecipientNameContainingIgnoreCase(recipientName.trim());
        String senderSearch = senderName.trim().toLowerCase();
        return byRecipient.stream()
                .filter(p -> p.getSender() != null && p.getSender().getUser() != null
                        && senderFullName(p).toLowerCase().contains(senderSearch))
                .toList();
    }

    private static String senderFullName(Parcel p) {
        var u = p.getSender().getUser();
        return (u.getFirstName() != null ? u.getFirstName() : "") + " " + (u.getLastName() != null ? u.getLastName() : "");
    }

    public Optional<Parcel> findById(Long id) {
        return parcelRepository.findById(id);
    }

    public Optional<Parcel> findByTrackingCode(String code) {
        return parcelRepository.findByTrackingCode(code);
    }

    /** Търсене по име на клиент: подател (първо/фамилия) или получател. */
    public List<Parcel> findByClientName(String name) {
        if (name == null || name.isBlank()) return List.of();
        String search = name.trim();
        var byRecipient = parcelRepository.findByRecipientNameContainingIgnoreCase(search);
        var bySender = parcelRepository.findBySenderNameContainingIgnoreCase(search);
        return java.util.stream.Stream.concat(byRecipient.stream(), bySender.stream())
                .distinct()
                .toList();
    }

    public Parcel save(Parcel parcel) {
        if (parcel.getTrackingCode() == null || parcel.getTrackingCode().isBlank()) {
            parcel.setTrackingCode(generateTrackingCode());
        }
        if (parcel.getPrice() == null) {
            parcel.setPrice(calculatePrice(parcel));
        }
        if (parcel.getPaymentType() == null) {
            parcel.setPaymentType(PaymentType.SENDER_PAYS);
        }
        if (parcel.getStatus() == ParcelStatus.DELIVERED
                && parcel.getPaymentType() == PaymentType.RECIPIENT_PAYS
                && parcel.getPaidAt() == null) {
            parcel.setPaidAt(parcel.getDeliveredAt() != null ? parcel.getDeliveredAt() : Instant.now());
        }
        return parcelRepository.save(parcel);
    }

    public void deleteById(Long id) {
        parcelRepository.deleteById(id);
    }

    public List<Parcel> findByRegisteredBy(Long employeeId) {
        return parcelRepository.findByRegisteredById(employeeId);
    }

    /** Пратки за доставка от даден куриер (още не доставени). */
    public List<Parcel> findToDeliverByCourier(Long courierId) {
        if (courierId == null) return List.of();
        return parcelRepository.findByCourierIdAndStatusNotIn(courierId,
                List.of(ParcelStatus.DELIVERED, ParcelStatus.CANCELED));
    }

    public List<Parcel> findBySender(Long clientId) {
        return parcelRepository.findBySenderId(clientId);
    }

    public List<Parcel> findByRecipient(Long clientId) {
        return parcelRepository.findByRecipientClientId(clientId);
    }

    public List<Parcel> findReceivedByRecipient(Long clientId) {
        return parcelRepository.findByRecipientClientIdAndStatus(clientId, ParcelStatus.DELIVERED);
    }

    public List<Parcel> findNotReceived() {
        return parcelRepository.findByStatusNotIn(List.of(ParcelStatus.DELIVERED, ParcelStatus.CANCELED));
    }

    public BigDecimal sumDeliveredBetween(Instant from, Instant to) {
        var delivered = parcelRepository.findByDeliveredAtBetweenAndStatus(from, to, ParcelStatus.DELIVERED);
        return delivered.stream()
                .map(p -> p.getPrice() != null ? p.getPrice() : BigDecimal.ZERO)
                .reduce(BigDecimal.ZERO, BigDecimal::add)
                .setScale(2, RoundingMode.HALF_UP);
    }

    /** Приходи по дата на плащане: сума от цените на пратки с paidAt в периода. */
    public BigDecimal sumPaidBetween(Instant from, Instant to) {
        var paid = parcelRepository.findByPaidAtNotNullAndPaidAtBetween(from, to);
        return paid.stream()
                .map(p -> p.getPrice() != null ? p.getPrice() : BigDecimal.ZERO)
                .reduce(BigDecimal.ZERO, BigDecimal::add)
                .setScale(2, RoundingMode.HALF_UP);
    }

    /** Пратки с плащане от получател, още не доставени – очаквано плащане при доставка/предаване. */
    public List<Parcel> findUnpaidParcels() {
        return parcelRepository.findByPaymentTypeAndStatusNotIn(PaymentType.RECIPIENT_PAYS,
                List.of(ParcelStatus.DELIVERED, ParcelStatus.CANCELED));
    }

    public BigDecimal sumUnpaidAmount() {
        return findUnpaidParcels().stream()
                .map(p -> p.getPrice() != null ? p.getPrice() : BigDecimal.ZERO)
                .reduce(BigDecimal.ZERO, BigDecimal::add)
                .setScale(2, RoundingMode.HALF_UP);
    }

    private String generateTrackingCode() {
        return "TRK-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
    }

    private BigDecimal calculatePrice(Parcel parcel) {
        BigDecimal base = (parcel.getDeliveryType() == DeliveryType.TO_ADDRESS)
                ? BASE_FEE_TO_ADDRESS
                : BASE_FEE_TO_OFFICE;
        if (parcel.getWeightKg() == null) return base.setScale(2, RoundingMode.HALF_UP);
        return base.add(PER_KG.multiply(parcel.getWeightKg())).setScale(2, RoundingMode.HALF_UP);
    }
}

