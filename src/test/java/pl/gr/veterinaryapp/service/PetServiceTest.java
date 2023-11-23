package pl.gr.veterinaryapp.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mapstruct.factory.Mappers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.User;
import pl.gr.veterinaryapp.exception.IncorrectDataException;
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
import pl.gr.veterinaryapp.service.impl.PetServiceImpl;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import static java.util.Collections.emptyList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowableOfType;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PetServiceTest {

    private static final Long PET_ID = 1L;
    private static final String PET_NAME = "Puszek";
    private static final Long ANIMAL_ID = 1L;
    private static final Long CLIENT_ID = 1L;
    private static final User USER = new User("name", "passwd", Collections.emptySet());

    private PetRepository petRepository;
    private ClientRepository clientRepository;
    private AnimalRepository animalRepository;
    private PetServiceImpl petService;
    private PetMapper petMapper;

    @BeforeEach
    void setup() {
        this.petRepository = Mockito.mock(PetRepository.class);
        this.clientRepository = Mockito.mock(ClientRepository.class);
        this.animalRepository = Mockito.mock(AnimalRepository.class);
        this.petMapper = Mappers.getMapper(PetMapper.class);
        this.petService = new PetServiceImpl(petRepository, clientRepository, animalRepository, petMapper);
    }

    @Test
    void deletePetById_WithCorrectId_Deleted() {
        Pet pet = new Pet();

        when(petRepository.findById(anyLong())).thenReturn(Optional.of(pet));

        petService.deletePet(PET_ID);

        verify(petRepository).findById(eq(PET_ID));
        verify(petRepository).delete(eq(pet));
        verifyNoInteractions(clientRepository, animalRepository);
    }

    @Test
    void deletePet_PetNotFound_ThrownException() {
        when(petRepository.findById(anyLong())).thenReturn(Optional.empty());

        ResourceNotFoundException thrown =
                catchThrowableOfType(() -> petService.deletePet(PET_ID), ResourceNotFoundException.class);

        assertThat(thrown)
                .hasMessage("Wrong id.");

        verify(petRepository).findById(eq(PET_ID));
        verifyNoInteractions(clientRepository, animalRepository);
    }

    @Test
    void getPetById_WithCorrectId_Returned() {
        Pet pet = new Pet();

        when(petRepository.findById(anyLong())).thenReturn(Optional.of(pet));

        PetResponseDto petResponseDto = petMapper.map(pet);
        var result = petService.getPetById(USER, PET_ID);

        assertThat(result)
                .isNotNull()
                .isEqualTo(petResponseDto);

        verify(petRepository).findById(eq(PET_ID));
        verifyNoInteractions(clientRepository, animalRepository);
    }

    @Test
    void getPetById_WithWrongId_ExceptionThrown() {
        when(petRepository.findById(anyLong())).thenReturn(Optional.empty());

        ResourceNotFoundException thrown =
                catchThrowableOfType(() -> petService.getPetById(USER, PET_ID), ResourceNotFoundException.class);

        assertThat(thrown)
                .hasMessage("Wrong id.");

        verify(petRepository).findById(eq(PET_ID));
        verifyNoInteractions(clientRepository, animalRepository);
    }

    @Test
    void getAllPets_ReturnPetsList_Returned() {
        List<Pet> pets = emptyList();

        when(petRepository.findAll()).thenReturn(pets);

        var result = petService.getAllPets(USER);

        assertThat(result)
                .isNotNull()
                .isEqualTo(pets);

        verify(petRepository).findAll();
        verifyNoInteractions(clientRepository, animalRepository);
    }

    @Test
    void createPet_WithCorrectData_Created() {
        LocalDate birthDate = LocalDate.now();

        PetRequestDto request = preparePetRequestDto(PET_NAME, ANIMAL_ID, CLIENT_ID, birthDate);

        var animal = new Animal();
        var client = new Client();

        when(clientRepository.findById(anyLong())).thenReturn(Optional.of(client));
        when(animalRepository.findById(anyLong())).thenReturn(Optional.of(animal));
        when(petRepository.save(any(Pet.class)))
                .thenAnswer(invocation -> {
                    Pet pet = invocation.getArgument(0);
                    pet.setId(PET_ID);
                    return pet;
                });

        var result = petService.createPet(USER, request);

        assertThat(result)
                .isNotNull()
                .matches(pet -> Objects.equals(pet.getId(), PET_ID))
                .matches(pet -> Objects.equals(pet.getBirthDate(), birthDate));

        verify(clientRepository).findById(eq(CLIENT_ID));
        verify(animalRepository).findById(eq(ANIMAL_ID));
        verify(petRepository).save(any(Pet.class));
    }

    @Test
    void createPet_WithNameNull_ExceptionThrown() {
        LocalDate birthDate = LocalDate.now();

        PetRequestDto request = preparePetRequestDto(null, ANIMAL_ID, CLIENT_ID, birthDate);

        IncorrectDataException thrown =
                catchThrowableOfType(() -> petService.createPet(USER, request), IncorrectDataException.class);

        assertThat(thrown)
                .hasMessage("Name cannot be null.");

        verifyNoInteractions(petRepository, animalRepository, clientRepository);
    }

    @Test
    void createPet_WithBirthDayNull_ExceptionThrown() {
        PetRequestDto request = preparePetRequestDto(PET_NAME, ANIMAL_ID, CLIENT_ID, null);

        IncorrectDataException thrown =
                catchThrowableOfType(() -> petService.createPet(USER, request), IncorrectDataException.class);

        assertThat(thrown)
                .hasMessage("Incorrect birth date provided");

        verifyNoInteractions(petRepository, animalRepository, clientRepository);
    }

    @Test
    void createPet_WithWrongAnimalId_ExceptionThrown() {
        LocalDate birthDate = LocalDate.now();

        PetRequestDto request = preparePetRequestDto(PET_NAME, ANIMAL_ID, CLIENT_ID, birthDate);

        when(animalRepository.findById(anyLong())).thenReturn(Optional.empty());

        ResourceNotFoundException thrown =
                catchThrowableOfType(() -> petService.createPet(USER, request), ResourceNotFoundException.class);

        assertThat(thrown)
                .hasMessage("Wrong id.");

        verify(animalRepository).findById(eq(ANIMAL_ID));
        verifyNoInteractions(petRepository, clientRepository);
    }

    @Test
    void createPet_WithWrongClientId_ExceptionThrown() {
        LocalDate birthDate = LocalDate.now();

        PetRequestDto request = preparePetRequestDto(PET_NAME, ANIMAL_ID, CLIENT_ID, birthDate);

        when(animalRepository.findById(anyLong())).thenReturn(Optional.of(new Animal()));
        when(clientRepository.findById(anyLong())).thenReturn(Optional.empty());

        ResourceNotFoundException thrown =
                catchThrowableOfType(() -> petService.createPet(USER, request), ResourceNotFoundException.class);

        assertThat(thrown)
                .hasMessage("Wrong id.");

        verify(animalRepository).findById(eq(ANIMAL_ID));
        verify(clientRepository).findById(eq(CLIENT_ID));
        verifyNoInteractions(petRepository);
    }

    private PetRequestDto preparePetRequestDto(String petName, Long animalId, Long clientId, LocalDate birthDate) {
        PetRequestDto request = new PetRequestDto();
        request.setName(petName);
        request.setAnimalId(animalId);
        request.setClientId(clientId);
        request.setBirthDate(birthDate);
        return request;
    }
}
