package com.nbu.CSCB532.service.logistics;

import com.nbu.CSCB532.model.logistics.Address;
import com.nbu.CSCB532.model.logistics.Client;
import com.nbu.CSCB532.model.logistics.ClientAddress;
import com.nbu.CSCB532.repository.logistics.AddressRepository;
import com.nbu.CSCB532.repository.logistics.ClientAddressRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ClientAddressService {

    private final ClientAddressRepository clientAddressRepository;
    private final AddressRepository addressRepository;
    private final ClientService clientService;

    public Optional<ClientAddress> findById(Long id) {
        return clientAddressRepository.findById(id);
    }

    public List<ClientAddress> findByClientId(Long clientId) {
        return clientAddressRepository.findByClientId(clientId);
    }

    /** Създава еднократен адрес (без клиент) за доставка. */
    public ClientAddress createOneOffAddress(String country, String city, String zipCode, String street, String details) {
        Address addr = Address.builder()
                .country(country)
                .city(city)
                .zipCode(zipCode)
                .street(street)
                .details(details)
                .build();
        addr = addressRepository.save(addr);
        ClientAddress ca = ClientAddress.builder()
                .client(null)
                .address(addr)
                .build();
        return clientAddressRepository.save(ca);
    }

    /** Създава адрес и го свързва с клиент (или еднократен ако clientId е null). */
    public ClientAddress createAddress(Long clientId, String country, String city, String zipCode, String street, String details) {
        Address addr = Address.builder()
                .country(country)
                .city(city)
                .zipCode(zipCode)
                .street(street)
                .details(details)
                .build();
        addr = addressRepository.save(addr);
        Client client = clientId != null ? clientService.findById(clientId).orElse(null) : null;
        ClientAddress ca = ClientAddress.builder()
                .client(client)
                .address(addr)
                .build();
        return clientAddressRepository.save(ca);
    }

    public ClientAddress save(ClientAddress clientAddress) {
        return clientAddressRepository.save(clientAddress);
    }
}
