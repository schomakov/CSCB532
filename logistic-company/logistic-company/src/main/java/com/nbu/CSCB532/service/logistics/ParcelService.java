package com.nbu.CSCB532.service.logistics;

import com.nbu.CSCB532.model.logistics.DeliveryType;
import com.nbu.CSCB532.model.logistics.Parcel;
import com.nbu.CSCB532.model.logistics.ParcelStatus;
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

    private static final BigDecimal BASE_FEE = new BigDecimal("5.00");
    private static final BigDecimal PER_KG = new BigDecimal("1.50");
    private static final BigDecimal ADDRESS_SURCHARGE = new BigDecimal("3.00");

    public List<Parcel> findAll() {
        return parcelRepository.findAll();
    }

    public Optional<Parcel> findById(Long id) {
        return parcelRepository.findById(id);
    }

    public Optional<Parcel> findByTrackingCode(String code) {
        return parcelRepository.findByTrackingCode(code);
    }

    public Parcel save(Parcel parcel) {
        if (parcel.getTrackingCode() == null || parcel.getTrackingCode().isBlank()) {
            parcel.setTrackingCode(generateTrackingCode());
        }
        if (parcel.getPrice() == null) {
            parcel.setPrice(calculatePrice(parcel));
        }
        return parcelRepository.save(parcel);
    }

    public void deleteById(Long id) {
        parcelRepository.deleteById(id);
    }

    public List<Parcel> findByRegisteredBy(Long employeeId) {
        return parcelRepository.findByRegisteredById(employeeId);
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

    private String generateTrackingCode() {
        return "TRK-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
    }

    private BigDecimal calculatePrice(Parcel parcel) {
        BigDecimal price = BASE_FEE.add(PER_KG.multiply(parcel.getWeightKg()));
        if (parcel.getDeliveryType() == DeliveryType.TO_ADDRESS) {
            price = price.add(ADDRESS_SURCHARGE);
        }
        return price.setScale(2, RoundingMode.HALF_UP);
    }
}

