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
import pl.gr.veterinaryapp.mapper.AnimalMapper;
import pl.gr.veterinaryapp.model.dto.AnimalRequestDto;
import pl.gr.veterinaryapp.model.dto.AnimalResponseDto;
import pl.gr.veterinaryapp.model.entity.Animal;
import pl.gr.veterinaryapp.repository.AnimalRepository;
import pl.gr.veterinaryapp.service.impl.AnimalServiceImpl;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowableOfType;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AnimalServiceTest {

    private static final Long ANIMAL_ID = 1L;
    private AnimalRepository animalRepository;
    private AnimalMapper mapper;
    private AnimalServiceImpl animalService;

    @BeforeEach
    void setup() {
        this.animalRepository = Mockito.mock(AnimalRepository.class);
        this.mapper = Mappers.getMapper(AnimalMapper.class);
        this.animalService = new AnimalServiceImpl(animalRepository, mapper);
    }

    @Test
    void getAnimalById_WithCorrectId_Returned() {
        Animal animal = new Animal();

        when(animalRepository.findById(anyLong())).thenReturn(Optional.of(animal));
        var dto = mapper.mapToDto(animal);

        var result = animalService.getAnimalById(ANIMAL_ID);

        assertThat(result)
                .isNotNull()
                .isEqualTo(dto);

        verify(animalRepository).findById(eq(ANIMAL_ID));
    }

    @Test
    void getAnimalById_WithWrongId_ExceptionThrown() {
        when(animalRepository.findById(anyLong())).thenReturn(Optional.empty());

        ResourceNotFoundException thrown =
                catchThrowableOfType(() -> animalService.getAnimalById(ANIMAL_ID), ResourceNotFoundException.class);

        assertThat(thrown)
                .hasMessage("Wrong id.");

        verify(animalRepository).findById(eq(ANIMAL_ID));
    }

    @Test
    void createAnimal_NewAnimal_Created() {
        AnimalRequestDto animalDTO = new AnimalRequestDto();
        animalDTO.setSpecies("test");
        Animal animal = new Animal();
        animal.setSpecies("test");

        when(animalRepository.findBySpecies(anyString())).thenReturn(Optional.empty());
        AnimalResponseDto animalResponseDto = mapper.mapToDto(animal);
        animalRepository.save(animal);

        var result = animalService.createAnimal(animalDTO);

        assertThat(result)
                .isNotNull()
                .isEqualTo(animalResponseDto);

        verify(animalRepository).save(eq(animal));
        verify(animalRepository).findBySpecies(eq("test"));
    }

    @Test
    void createAnimal_ExistsAnimal_ExceptionThrown() {
        AnimalRequestDto animalDTO = new AnimalRequestDto();
        animalDTO.setSpecies("test");
        Animal animal = new Animal();
        animal.setSpecies("test");

        when(animalRepository.findBySpecies(anyString())).thenReturn(Optional.of(animal));

        IncorrectDataException thrown =
                catchThrowableOfType(() -> animalService.createAnimal(animalDTO), IncorrectDataException.class);

        assertThat(thrown)
                .hasMessage("Species exists.");

        verify(animalRepository).findBySpecies(eq("test"));
    }

    @Test
    void deleteAnimal_ExistsAnimal_Deleted() {
        Animal animal = new Animal();

        when(animalRepository.findById(anyLong())).thenReturn(Optional.of(animal));

        animalService.deleteAnimal(ANIMAL_ID);

        verify(animalRepository).findById(eq(ANIMAL_ID));
        verify(animalRepository).delete(eq(animal));
    }

    @Test
    void deleteAnimal_AnimalNotFound_ThrownException() {

        when(animalRepository.findById(anyLong())).thenReturn(Optional.empty());

        ResourceNotFoundException thrown =
                catchThrowableOfType(() -> animalService.deleteAnimal(ANIMAL_ID), ResourceNotFoundException.class);

        assertThat(thrown)
                .hasMessage("Wrong id.");

        verify(animalRepository).findById(eq(ANIMAL_ID));
    }

    @Test
    void getAllAnimals_ReturnAnimals_Returned() {
        List<Animal> animals = new ArrayList<>();

        when(animalRepository.findAll()).thenReturn(animals);

        var result = animalService.getAllAnimals();

        assertThat(result)
                .isNotNull();

        verify(animalRepository).findAll();
    }
}