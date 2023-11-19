package pl.gr.veterinaryapp.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pl.gr.veterinaryapp.exception.IncorrectDataException;
import pl.gr.veterinaryapp.exception.ResourceNotFoundException;
import pl.gr.veterinaryapp.mapper.ClientMapper;
import pl.gr.veterinaryapp.model.dto.ClientRequestDto;
import pl.gr.veterinaryapp.model.dto.ClientResponseDto;
import pl.gr.veterinaryapp.model.entity.Client;
import pl.gr.veterinaryapp.model.entity.VetAppUser;
import pl.gr.veterinaryapp.repository.ClientRepository;
import pl.gr.veterinaryapp.repository.UserRepository;
import pl.gr.veterinaryapp.service.ClientService;
import pl.gr.veterinaryapp.service.validator.DataValidator;

import java.util.List;
import java.util.stream.Collectors;

import static pl.gr.veterinaryapp.service.validator.DataValidator.*;

@RequiredArgsConstructor
@Service
public class ClientServiceImpl implements ClientService {

    private final ClientRepository clientRepository;
    private final ClientMapper mapper;
    private final UserRepository userRepository;

    @Override
    public ClientResponseDto getClientById(Long id) {
        Client client = findByIdOrThrow(id, clientRepository);
        return mapper.map(client);
    }

    @Override
    public List<ClientResponseDto> getAllClients() {
        return clientRepository.findAll().stream()
                .map(mapper::map)
                .collect(Collectors.toList());
    }

    @Transactional
    @Override
    public ClientResponseDto createClient(ClientRequestDto clientRequestDTO) {
        checkData(clientRequestDTO.getName(), clientRequestDTO.getSurname());
        VetAppUser user = userRepository.findByUsername(clientRequestDTO.getUsername())
                .orElse(null);

        Client client = mapper.map(clientRequestDTO);
        client.setUser(user);
        clientRepository.save(client);

        return client;
    }

    @Transactional
    @Override
    public void deleteClient(Long id) {
        Client result = clientRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Wrong id."));
        clientRepository.delete(result);
    }
}
