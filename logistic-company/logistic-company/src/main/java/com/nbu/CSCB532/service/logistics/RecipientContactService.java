package com.nbu.CSCB532.service.logistics;

import com.nbu.CSCB532.model.logistics.Client;
import com.nbu.CSCB532.model.logistics.RecipientContact;
import com.nbu.CSCB532.repository.logistics.RecipientContactRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class RecipientContactService {

    private final RecipientContactRepository recipientContactRepository;
    private final ClientService clientService;

    public Optional<RecipientContact> findById(Long id) {
        return recipientContactRepository.findById(id);
    }

    public List<RecipientContact> findByClientId(Long clientId) {
        return recipientContactRepository.findByClientId(clientId);
    }

    /** Връща или създава контакт за даден клиент (име от User, телефон от Client). */
    public RecipientContact findOrCreateForClient(Long clientId) {
        Client client = clientService.findById(clientId).orElse(null);
        if (client == null) return null;
        List<RecipientContact> existing = recipientContactRepository.findByClientId(clientId);
        if (!existing.isEmpty()) return existing.get(0);
        String name = client.getUser() != null
                ? (client.getUser().getFirstName() + " " + client.getUser().getLastName()).trim()
                : "Client " + clientId;
        RecipientContact contact = RecipientContact.builder()
                .name(name)
                .phone(client.getPhone())
                .client(client)
                .build();
        return recipientContactRepository.save(contact);
    }

    public RecipientContact save(String name, String phone, Long clientId) {
        Client client = clientId != null ? clientService.findById(clientId).orElse(null) : null;
        RecipientContact contact = RecipientContact.builder()
                .name(name != null ? name.trim() : "")
                .phone(phone != null ? phone.trim() : null)
                .client(client)
                .build();
        return recipientContactRepository.save(contact);
    }

    public RecipientContact save(RecipientContact contact) {
        return recipientContactRepository.save(contact);
    }
}
