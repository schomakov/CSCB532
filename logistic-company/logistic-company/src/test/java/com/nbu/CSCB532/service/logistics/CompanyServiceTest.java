package com.nbu.CSCB532.service.logistics;

import com.nbu.CSCB532.model.logistics.Company;
import com.nbu.CSCB532.repository.logistics.CompanyRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CompanyServiceTest {

    @Mock
    private CompanyRepository companyRepository;

    @InjectMocks
    private CompanyService companyService;

    private Company company;

    @BeforeEach
    void setUp() {
        company = Company.builder()
                .id(1L)
                .name("Test Company")
                .registrationNumber("123456789")
                .createdAt(Instant.now())
                .updatedAt(Instant.now())
                .build();
    }

    @Test
    void findAll_returnsAllCompanies() {
        when(companyRepository.findAll()).thenReturn(List.of(company));
        List<Company> result = companyService.findAll();
        assertThat(result).hasSize(1).containsExactly(company);
        verify(companyRepository).findAll();
    }

    @Test
    void findById_whenExists_returnsCompany() {
        when(companyRepository.findById(1L)).thenReturn(Optional.of(company));
        assertThat(companyService.findById(1L)).contains(company);
        verify(companyRepository).findById(1L);
    }

    @Test
    void findById_whenNotExists_returnsEmpty() {
        when(companyRepository.findById(999L)).thenReturn(Optional.empty());
        assertThat(companyService.findById(999L)).isEmpty();
    }

    @Test
    void save_persistsCompany() {
        when(companyRepository.save(any(Company.class))).thenReturn(company);
        Company saved = companyService.save(company);
        assertThat(saved).isEqualTo(company);
        verify(companyRepository).save(company);
    }

    @Test
    void deleteById_callsRepository() {
        companyService.deleteById(1L);
        verify(companyRepository).deleteById(1L);
    }
}
