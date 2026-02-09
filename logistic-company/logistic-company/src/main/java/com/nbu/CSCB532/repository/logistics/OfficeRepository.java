package com.nbu.CSCB532.repository.logistics;

import com.nbu.CSCB532.model.logistics.Office;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface OfficeRepository extends JpaRepository<Office, Long> {
    List<Office> findByCompanyId(Long companyId);
}

