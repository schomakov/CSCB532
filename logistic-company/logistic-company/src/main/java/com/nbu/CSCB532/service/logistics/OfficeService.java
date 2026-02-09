package com.nbu.CSCB532.service.logistics;

import com.nbu.CSCB532.model.logistics.Office;
import com.nbu.CSCB532.repository.logistics.OfficeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class OfficeService {
    private final OfficeRepository officeRepository;

    public List<Office> findAll() {
        return officeRepository.findAll();
    }

    public List<Office> findByCompany(Long companyId) {
        return officeRepository.findByCompanyId(companyId);
    }

    public Optional<Office> findById(Long id) {
        return officeRepository.findById(id);
    }

    public Office save(Office office) {
        return officeRepository.save(office);
    }

    public void deleteById(Long id) {
        officeRepository.deleteById(id);
    }
}

