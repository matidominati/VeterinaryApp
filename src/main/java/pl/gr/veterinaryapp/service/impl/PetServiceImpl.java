package pl.gr.veterinaryapp.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pl.gr.veterinaryapp.exception.ResourceNotFoundException;
import pl.gr.veterinaryapp.mapper.PetMapper;
import pl.gr.veterinaryapp.model.dto.PetRequestDto;
import pl.gr.veterinaryapp.model.dto.PetResponseDto;
import pl.gr.veterinaryapp.model.entity.Animal;
import pl.gr.veterinaryapp.model.entity.Client;
import pl.gr.veterinaryapp.model.entity.Pet;
import pl.gr.veterinaryapp.repository.AnimalRepository;
import pl.gr.veterinaryapp.repository.ClientRepository;
import pl.gr.veterinaryapp.repository.PetRepository;
import pl.gr.veterinaryapp.service.PetService;

import java.util.List;
import java.util.stream.Collectors;

import static pl.gr.veterinaryapp.service.validator.DataValidator.*;

@RequiredArgsConstructor
@Service
public class PetServiceImpl implements PetService {

    private final PetRepository petRepository;
    private final ClientRepository clientRepository;
    private final AnimalRepository animalRepository;
    private final PetMapper mapper;

    @Override
    public List<PetResponseDto> getAllPets(User user) {
        return petRepository.findAll()
                .stream()
                .filter(pet -> isUserAuthorized(user, pet.getClient()))
                .map(mapper::map)
                .collect(Collectors.toList());
    }

    @Override
    public PetResponseDto getPetById(User user, Long id) {
        Pet pet = findByIdOrThrow(id, petRepository);
        if (!isUserAuthorized(user, pet.getClient())) {
            throw new ResourceNotFoundException("Wrong id.");
        }
        return mapper.map(pet);
    }

    @Transactional
    @Override
    public PetResponseDto createPet(User user, PetRequestDto petRequestDto) {
        validateName(petRequestDto.getName());
        validateBirthDate(petRequestDto.getBirthDate());
        Animal animal = findByIdOrThrow(petRequestDto.getAnimalId(), animalRepository);
        Client client = findByIdOrThrow(petRequestDto.getClientId(), clientRepository);
        if (!isUserAuthorized(user, client)) {
            throw new ResourceNotFoundException("User don't have access to this pet");
        }
        var newPet = new Pet();
        newPet.setName(petRequestDto.getName());
        newPet.setBirthDate(petRequestDto.getBirthDate());
        newPet.setAnimal(animal);
        newPet.setClient(client);
        petRepository.save(newPet);
        return mapper.map(newPet);
    }

    @Transactional
    @Override
    public void deletePet(Long id) {
        Pet result = findByIdOrThrow(id, petRepository);
        petRepository.delete(result);
    }

    private boolean isUserAuthorized(User user, Client client) {
        boolean isClient = user.getAuthorities()
                .stream()
                .map(GrantedAuthority::getAuthority)
                .anyMatch("ROLE_CLIENT"::equalsIgnoreCase);
        if (isClient) {
            if (client.getUser() == null) {
                return false;
            } else {
                return client.getUser().getUsername().equalsIgnoreCase(user.getUsername());
            }
        }
        return true;
    }
}
