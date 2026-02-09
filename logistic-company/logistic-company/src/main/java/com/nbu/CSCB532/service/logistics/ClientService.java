package com.nbu.CSCB532.service.logistics;

import com.nbu.CSCB532.model.logistics.Client;
import com.nbu.CSCB532.repository.logistics.ClientRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ClientService {
    private final ClientRepository clientRepository;

    public List<Client> findAll() {
        return clientRepository.findAll();
    }

    public Optional<Client> findById(Long id) {
        return clientRepository.findById(id);
    }

    public Client save(Client c) {
        return clientRepository.save(c);
    }

    public void deleteById(Long id) {
        clientRepository.deleteById(id);
    }
}

