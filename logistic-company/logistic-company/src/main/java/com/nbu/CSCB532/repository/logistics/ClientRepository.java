package com.nbu.CSCB532.repository.logistics;

import com.nbu.CSCB532.model.logistics.Client;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ClientRepository extends JpaRepository<Client, Long> {
}

