package com.nbu.CSCB532.service.logistics;

import com.nbu.CSCB532.model.logistics.*;
import com.nbu.CSCB532.repository.logistics.ParcelRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ParcelServiceTest {

    @Mock
    private ParcelRepository parcelRepository;

    @InjectMocks
    private ParcelService parcelService;

    private Parcel parcel;

    @BeforeEach
    void setUp() {
        parcel = Parcel.builder()
                .id(1L)
                .deliveryType(DeliveryType.TO_OFFICE)
                .weightKg(new BigDecimal("2.00"))
                .status(ParcelStatus.REGISTERED)
                .trackingCode("TRK-ABC12345")
                .createdAt(Instant.now())
                .updatedAt(Instant.now())
                .build();
    }

    @Test
    void findAll_returnsAllParcels() {
        when(parcelRepository.findAll()).thenReturn(List.of(parcel));
        List<Parcel> result = parcelService.findAll();
        assertThat(result).hasSize(1).containsExactly(parcel);
        verify(parcelRepository).findAll();
    }

    @Test
    void findById_whenExists_returnsParcel() {
        when(parcelRepository.findById(1L)).thenReturn(Optional.of(parcel));
        assertThat(parcelService.findById(1L)).contains(parcel);
    }

    @Test
    void save_generatesTrackingCodeWhenMissing() {
        parcel.setTrackingCode(null);
        when(parcelRepository.save(any(Parcel.class))).thenAnswer(inv -> inv.getArgument(0));
        parcelService.save(parcel);
        ArgumentCaptor<Parcel> captor = ArgumentCaptor.forClass(Parcel.class);
        verify(parcelRepository).save(captor.capture());
        assertThat(captor.getValue().getTrackingCode()).startsWith("TRK-").hasSize(12);
    }

    @Test
    void save_calculatesPriceWhenNull_toOffice() {
        parcel.setPrice(null);
        parcel.setDeliveryType(DeliveryType.TO_OFFICE);
        parcel.setWeightKg(new BigDecimal("2.00"));
        when(parcelRepository.save(any(Parcel.class))).thenAnswer(inv -> inv.getArgument(0));
        parcelService.save(parcel);
        ArgumentCaptor<Parcel> captor = ArgumentCaptor.forClass(Parcel.class);
        verify(parcelRepository).save(captor.capture());
        // BASE_FEE_TO_OFFICE 5 + 4*weight = 5 + 8 = 13.00
        assertThat(captor.getValue().getPrice()).isEqualByComparingTo(new BigDecimal("13.00"));
    }

    @Test
    void save_calculatesPriceWhenNull_toAddress() {
        parcel.setPrice(null);
        parcel.setDeliveryType(DeliveryType.TO_ADDRESS);
        parcel.setWeightKg(new BigDecimal("1.00"));
        when(parcelRepository.save(any(Parcel.class))).thenAnswer(inv -> inv.getArgument(0));
        parcelService.save(parcel);
        ArgumentCaptor<Parcel> captor = ArgumentCaptor.forClass(Parcel.class);
        verify(parcelRepository).save(captor.capture());
        // BASE_FEE_TO_ADDRESS 7.50 + 4*weight = 7.50 + 4 = 11.50
        assertThat(captor.getValue().getPrice()).isEqualByComparingTo(new BigDecimal("11.50"));
    }

    @Test
    void findByRegisteredBy_returnsParcelsByEmployee() {
        when(parcelRepository.findByRegisteredById(10L)).thenReturn(List.of(parcel));
        List<Parcel> result = parcelService.findByRegisteredBy(10L);
        assertThat(result).hasSize(1).containsExactly(parcel);
    }

    @Test
    void findBySender_returnsParcelsBySenderClient() {
        when(parcelRepository.findBySenderId(5L)).thenReturn(List.of(parcel));
        List<Parcel> result = parcelService.findBySender(5L);
        assertThat(result).hasSize(1).containsExactly(parcel);
    }

    @Test
    void findReceivedByRecipient_returnsDeliveredParcelsForClient() {
        when(parcelRepository.findByRecipientClientIdAndStatus(5L, ParcelStatus.DELIVERED))
                .thenReturn(List.of(parcel));
        List<Parcel> result = parcelService.findReceivedByRecipient(5L);
        assertThat(result).hasSize(1).containsExactly(parcel);
    }

    @Test
    void findNotReceived_returnsParcelsNotDeliveredOrCanceled() {
        when(parcelRepository.findByStatusNotIn(List.of(ParcelStatus.DELIVERED, ParcelStatus.CANCELED)))
                .thenReturn(List.of(parcel));
        List<Parcel> result = parcelService.findNotReceived();
        assertThat(result).hasSize(1).containsExactly(parcel);
    }

    @Test
    void sumDeliveredBetween_returnsSumOfPrices() {
        Parcel p1 = Parcel.builder().price(new BigDecimal("10.00")).build();
        Parcel p2 = Parcel.builder().price(new BigDecimal("15.50")).build();
        when(parcelRepository.findByDeliveredAtBetweenAndStatus(any(Instant.class), any(Instant.class), eq(ParcelStatus.DELIVERED)))
                .thenReturn(List.of(p1, p2));
        BigDecimal sum = parcelService.sumDeliveredBetween(Instant.EPOCH, Instant.now());
        assertThat(sum).isEqualByComparingTo(new BigDecimal("25.50"));
    }

    @Test
    void sumPaidBetween_returnsSumOfPricesForPaidInPeriod() {
        Parcel p1 = Parcel.builder().price(new BigDecimal("20.00")).build();
        Parcel p2 = Parcel.builder().price(new BigDecimal("30.00")).build();
        when(parcelRepository.findByPaidAtNotNullAndPaidAtBetween(any(Instant.class), any(Instant.class)))
                .thenReturn(List.of(p1, p2));
        BigDecimal sum = parcelService.sumPaidBetween(Instant.EPOCH, Instant.now());
        assertThat(sum).isEqualByComparingTo(new BigDecimal("50.00"));
    }

    @Test
    void deleteById_callsRepository() {
        parcelService.deleteById(1L);
        verify(parcelRepository).deleteById(1L);
    }

    @Test
    void findByTrackingCode_whenExists_returnsParcel() {
        when(parcelRepository.findByTrackingCode("TRK-ABC12345")).thenReturn(Optional.of(parcel));
        assertThat(parcelService.findByTrackingCode("TRK-ABC12345")).contains(parcel);
    }

    @Test
    void findByTrackingCode_whenNotExists_returnsEmpty() {
        when(parcelRepository.findByTrackingCode("TRK-UNKNOWN")).thenReturn(Optional.empty());
        assertThat(parcelService.findByTrackingCode("TRK-UNKNOWN")).isEmpty();
    }

    @Test
    void findToDeliverByCourier_returnsParcelsNotDeliveredOrCanceled() {
        when(parcelRepository.findByCourierIdAndStatusNotIn(5L, List.of(ParcelStatus.DELIVERED, ParcelStatus.CANCELED)))
                .thenReturn(List.of(parcel));
        List<Parcel> result = parcelService.findToDeliverByCourier(5L);
        assertThat(result).hasSize(1).containsExactly(parcel);
    }

    @Test
    void findToDeliverByCourier_nullId_returnsEmpty() {
        assertThat(parcelService.findToDeliverByCourier(null)).isEmpty();
        verify(parcelRepository, never()).findByCourierIdAndStatusNotIn(any(), any());
    }

    @Test
    void findUnpaidParcels_returnsRecipientPaysNotDelivered() {
        when(parcelRepository.findByPaymentTypeAndStatusNotIn(PaymentType.RECIPIENT_PAYS,
                List.of(ParcelStatus.DELIVERED, ParcelStatus.CANCELED))).thenReturn(List.of(parcel));
        List<Parcel> result = parcelService.findUnpaidParcels();
        assertThat(result).hasSize(1).containsExactly(parcel);
    }

    @Test
    void sumUnpaidAmount_sumsPricesOfUnpaidParcels() {
        Parcel p1 = Parcel.builder().price(new BigDecimal("10.00")).build();
        Parcel p2 = Parcel.builder().price(new BigDecimal("15.00")).build();
        when(parcelRepository.findByPaymentTypeAndStatusNotIn(PaymentType.RECIPIENT_PAYS,
                List.of(ParcelStatus.DELIVERED, ParcelStatus.CANCELED))).thenReturn(List.of(p1, p2));
        assertThat(parcelService.sumUnpaidAmount()).isEqualByComparingTo(new BigDecimal("25.00"));
    }
}
