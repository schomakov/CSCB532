package com.nbu.CSCB532.repository.logistics;

import com.nbu.CSCB532.model.logistics.ClientAddress;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ClientAddressRepository extends JpaRepository<ClientAddress, Long> {

    List<ClientAddress> findByClientId(Long clientId);
}
