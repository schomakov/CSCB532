package com.nbu.CSCB532.repository.logistics;

import com.nbu.CSCB532.model.logistics.RecipientContact;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface RecipientContactRepository extends JpaRepository<RecipientContact, Long> {

    List<RecipientContact> findByClientId(Long clientId);
}
