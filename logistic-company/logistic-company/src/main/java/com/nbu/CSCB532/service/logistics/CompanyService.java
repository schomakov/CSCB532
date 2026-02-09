package com.nbu.CSCB532.service.logistics;

import com.nbu.CSCB532.model.logistics.Company;
import com.nbu.CSCB532.repository.logistics.CompanyRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CompanyService {
    private final CompanyRepository companyRepository;

    public List<Company> findAll() {
        return companyRepository.findAll();
    }

    public Optional<Company> findById(Long id) {
        return companyRepository.findById(id);
    }

    public Company save(Company c) {
        return companyRepository.save(c);
    }

    public void deleteById(Long id) {
        companyRepository.deleteById(id);
    }
}

