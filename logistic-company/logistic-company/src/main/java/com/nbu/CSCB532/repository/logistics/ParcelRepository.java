package com.nbu.CSCB532.repository.logistics;

import com.nbu.CSCB532.model.logistics.Parcel;
import com.nbu.CSCB532.model.logistics.ParcelStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.time.Instant;

public interface ParcelRepository extends JpaRepository<Parcel, Long> {
    Optional<Parcel> findByTrackingCode(String trackingCode);
    List<Parcel> findByStatus(ParcelStatus status);
    List<Parcel> findBySenderId(Long senderId);
    List<Parcel> findByRegisteredById(Long employeeId);
    List<Parcel> findByRecipientClientId(Long clientId);
    List<Parcel> findByStatusNotIn(List<ParcelStatus> statuses);
    List<Parcel> findByDeliveredAtBetweenAndStatus(Instant from, Instant to, ParcelStatus status);
    List<Parcel> findByRecipientClientIdAndStatus(Long clientId, ParcelStatus status);
}

