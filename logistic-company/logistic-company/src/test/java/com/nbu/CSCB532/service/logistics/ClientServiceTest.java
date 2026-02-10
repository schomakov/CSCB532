package com.nbu.CSCB532.service.logistics;

import com.nbu.CSCB532.model.logistics.Client;
import com.nbu.CSCB532.repository.logistics.ClientRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ClientServiceTest {

    @Mock
    private ClientRepository clientRepository;

    @InjectMocks
    private ClientService clientService;

    private Client client;

    @BeforeEach
    void setUp() {
        client = Client.builder().id(1L).phone("+359888123456").build();
    }

    @Test
    void findAll_returnsAllClients() {
        when(clientRepository.findAll()).thenReturn(List.of(client));
        List<Client> result = clientService.findAll();
        assertThat(result).hasSize(1).containsExactly(client);
        verify(clientRepository).findAll();
    }

    @Test
    void findById_whenExists_returnsClient() {
        when(clientRepository.findById(1L)).thenReturn(Optional.of(client));
        assertThat(clientService.findById(1L)).contains(client);
    }

    @Test
    void findById_whenNotExists_returnsEmpty() {
        when(clientRepository.findById(999L)).thenReturn(Optional.empty());
        assertThat(clientService.findById(999L)).isEmpty();
    }

    @Test
    void save_persistsClient() {
        when(clientRepository.save(any(Client.class))).thenReturn(client);
        Client saved = clientService.save(client);
        assertThat(saved).isEqualTo(client);
        verify(clientRepository).save(client);
    }

    @Test
    void deleteById_callsRepository() {
        clientService.deleteById(1L);
        verify(clientRepository).deleteById(1L);
    }
}
