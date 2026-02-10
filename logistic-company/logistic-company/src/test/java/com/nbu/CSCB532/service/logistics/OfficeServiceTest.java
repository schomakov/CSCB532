package com.nbu.CSCB532.service.logistics;

import com.nbu.CSCB532.model.logistics.Office;
import com.nbu.CSCB532.repository.logistics.OfficeRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class OfficeServiceTest {

    @Mock
    private OfficeRepository officeRepository;

    @InjectMocks
    private OfficeService officeService;

    private Office office;

    @BeforeEach
    void setUp() {
        office = Office.builder().id(1L).name("Main Office").phone("+35921234567").active(true).build();
    }

    @Test
    void findAll_returnsAllOffices() {
        when(officeRepository.findAll()).thenReturn(List.of(office));
        List<Office> result = officeService.findAll();
        assertThat(result).hasSize(1).containsExactly(office);
        verify(officeRepository).findAll();
    }

    @Test
    void findById_whenExists_returnsOffice() {
        when(officeRepository.findById(1L)).thenReturn(Optional.of(office));
        assertThat(officeService.findById(1L)).contains(office);
    }

    @Test
    void findByCompany_returnsOfficesOfCompany() {
        when(officeRepository.findByCompanyId(1L)).thenReturn(List.of(office));
        List<Office> result = officeService.findByCompany(1L);
        assertThat(result).hasSize(1).containsExactly(office);
    }

    @Test
    void save_persistsOffice() {
        when(officeRepository.save(any(Office.class))).thenReturn(office);
        Office saved = officeService.save(office);
        assertThat(saved).isEqualTo(office);
        verify(officeRepository).save(office);
    }

    @Test
    void deleteById_callsRepository() {
        officeService.deleteById(1L);
        verify(officeRepository).deleteById(1L);
    }
}
