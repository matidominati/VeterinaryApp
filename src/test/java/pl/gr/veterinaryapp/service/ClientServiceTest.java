package pl.gr.veterinaryapp.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mapstruct.factory.Mappers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import pl.gr.veterinaryapp.exception.IncorrectDataException;
import pl.gr.veterinaryapp.exception.ResourceNotFoundException;
import pl.gr.veterinaryapp.mapper.ClientMapper;
import pl.gr.veterinaryapp.model.dto.ClientRequestDto;
import pl.gr.veterinaryapp.model.dto.ClientResponseDto;
import pl.gr.veterinaryapp.model.entity.Client;
import pl.gr.veterinaryapp.model.entity.VetAppUser;
import pl.gr.veterinaryapp.repository.ClientRepository;
import pl.gr.veterinaryapp.repository.UserRepository;
import pl.gr.veterinaryapp.service.impl.ClientServiceImpl;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowableOfType;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ClientServiceTest {

    private static final Long CLIENT_ID = 1L;
    private static final String CLIENT_NAME = "Konrad";
    private static final String CLIENT_SURNAME = "Gabrukiewicz";

    private ClientRepository clientRepository;

    private ClientMapper mapper;

    private UserRepository userRepository;

    private ClientServiceImpl clientService;
    @BeforeEach
    void setup() {
        this.clientRepository = Mockito.mock(ClientRepository.class);
        this.mapper = Mappers.getMapper(ClientMapper.class);
        this.userRepository = Mockito.mock(UserRepository.class);
        this.clientService = new ClientServiceImpl(clientRepository,mapper,userRepository);

    }
    @Test
    void getClientById_WithCorrectId_Returned() {
        Client client = new Client();

        when(clientRepository.findById(anyLong())).thenReturn(Optional.of(client));

        ClientResponseDto clientResponseDto = mapper.map(client);
        var result = clientService.getClientById(CLIENT_ID);

        assertThat(result)
                .isNotNull()
                .isEqualTo(clientResponseDto);

        verify(clientRepository).findById(eq(CLIENT_ID));

    }

    @Test
    void getClientById_WithWrongId_ExceptionThrown() {
        when(clientRepository.findById(anyLong())).thenReturn(Optional.empty());

        ResourceNotFoundException thrown =
                catchThrowableOfType(() -> clientService.getClientById(CLIENT_ID), ResourceNotFoundException.class);

        assertThat(thrown)
                .hasMessage("Wrong id.");

        verify(clientRepository).findById(eq(CLIENT_ID));
    }

    @Test
    void createClient_NewClient_Created() {
        VetAppUser user = new VetAppUser();
        ClientRequestDto clientDTO = new ClientRequestDto();
        clientDTO.setName(CLIENT_NAME);
        clientDTO.setSurname(CLIENT_SURNAME);
        clientDTO.setUsername("xyz");
        Client client = new Client();
        client.setName(CLIENT_NAME);
        client.setSurname(CLIENT_SURNAME);

        when(userRepository.findByUsername(clientDTO.getUsername())).thenReturn(Optional.of(user));
        client.setUser(user);
        var clientResponse = mapper.map(client);
        clientRepository.save(client);

        var result = clientService.createClient(clientDTO);

        assertThat(result)
                .isNotNull()
                .isEqualTo(clientResponse);

        verify(clientRepository).save(eq(client));
    }

    @Test
    void createClient_NullName_ExceptionThrown() {
        ClientRequestDto clientDTO = new ClientRequestDto();
        clientDTO.setName(null);
        clientDTO.setSurname(CLIENT_SURNAME);

        IncorrectDataException thrown =
                catchThrowableOfType(() -> clientService.createClient(clientDTO), IncorrectDataException.class);

        assertThat(thrown)
                .hasMessage("Name and Surname should not be null.");

    }

    @Test
    void deleteClient_ExistsClient_Deleted() {
        Client client = new Client();

        when(clientRepository.findById(anyLong())).thenReturn(Optional.of(client));

        clientService.deleteClient(CLIENT_ID);

        verify(clientRepository).findById(eq(CLIENT_ID));
        verify(clientRepository).delete(eq(client));
    }

    @Test
    void deleteClient_ClientNotFound_ThrownException() {
        when(clientRepository.findById(anyLong())).thenReturn(Optional.empty());

        ResourceNotFoundException thrown =
                catchThrowableOfType(() -> clientService.deleteClient(CLIENT_ID), ResourceNotFoundException.class);

        assertThat(thrown)
                .hasMessage("Wrong id.");

        verify(clientRepository).findById(eq(CLIENT_ID));
    }

    @Test
    void getAllClients_ReturnClients_Returned() {
        List<Client> clients = new ArrayList<>();

        when(clientRepository.findAll()).thenReturn(clients);

        var result = clientService.getAllClients();

        assertThat(result)
                .isNotNull();

        verify(clientRepository).findAll();
    }
}