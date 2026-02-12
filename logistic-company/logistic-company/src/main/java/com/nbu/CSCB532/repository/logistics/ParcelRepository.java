package com.nbu.CSCB532.repository.logistics;

import com.nbu.CSCB532.model.logistics.Parcel;
import com.nbu.CSCB532.model.logistics.ParcelStatus;
import com.nbu.CSCB532.model.logistics.PaymentType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;
import java.time.Instant;

public interface ParcelRepository extends JpaRepository<Parcel, Long> {
    Optional<Parcel> findByTrackingCode(String trackingCode);

    List<Parcel> findByRecipientContact_NameContainingIgnoreCase(String name);

    @Query("SELECT DISTINCT p FROM Parcel p JOIN p.sender s JOIN s.user u WHERE LOWER(CONCAT(COALESCE(u.firstName,''), ' ', COALESCE(u.lastName,''))) LIKE LOWER(CONCAT('%', :senderName, '%'))")
    List<Parcel> findBySenderNameContainingIgnoreCase(@Param("senderName") String senderName);

    List<Parcel> findByStatus(ParcelStatus status);
    List<Parcel> findByPaymentTypeAndStatusNotIn(PaymentType paymentType, List<ParcelStatus> statuses);
    List<Parcel> findByCourierIdAndStatusNotIn(Long courierId, List<ParcelStatus> statuses);
    List<Parcel> findBySenderId(Long senderId);
    List<Parcel> findByRegisteredById(Long employeeId);
    List<Parcel> findByRecipientContact_ClientId(Long clientId);
    List<Parcel> findByStatusNotIn(List<ParcelStatus> statuses);
    List<Parcel> findByDeliveredAtBetweenAndStatus(Instant from, Instant to, ParcelStatus status);
    List<Parcel> findByRecipientContact_ClientIdAndStatus(Long clientId, ParcelStatus status);
    /** Пратки с плащане в периода (по дата на плащане) – за калкулиране на приходи. */
    List<Parcel> findByPaidAtNotNullAndPaidAtBetween(Instant from, Instant to);
}

