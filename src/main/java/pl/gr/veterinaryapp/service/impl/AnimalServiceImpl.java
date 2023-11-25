package pl.gr.veterinaryapp.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pl.gr.veterinaryapp.exception.IncorrectDataException;
import pl.gr.veterinaryapp.exception.ResourceNotFoundException;
import pl.gr.veterinaryapp.mapper.AnimalMapper;
import pl.gr.veterinaryapp.model.dto.AnimalRequestDto;
import pl.gr.veterinaryapp.model.dto.AnimalResponseDto;
import pl.gr.veterinaryapp.model.entity.Animal;
import pl.gr.veterinaryapp.repository.AnimalRepository;
import pl.gr.veterinaryapp.service.AnimalService;
import pl.gr.veterinaryapp.service.validator.DataValidator;

import java.util.List;
import java.util.stream.Collectors;

import static pl.gr.veterinaryapp.service.validator.DataValidator.*;

@RequiredArgsConstructor
@Service
public class AnimalServiceImpl implements AnimalService {

    private final AnimalRepository animalRepository;
    private final AnimalMapper mapper;

    @Override
    public AnimalResponseDto getAnimalById(Long id) {
        Animal animal = findByIdOrThrow(id, animalRepository);
        return mapper.mapToDto(animal);
    }

    @Transactional
    @Override
    public AnimalResponseDto createAnimal(AnimalRequestDto animalRequestDto) {
        var animal = animalRepository.findBySpecies(animalRequestDto.getSpecies());
        if (animal.isPresent()) {
            throw new IncorrectDataException("Species exists.");
        }
        var newAnimal = mapper.map(animalRequestDto);
        animalRepository.save(newAnimal);
        return mapper.mapToDto(newAnimal);
    }

    @Transactional
    @Override
    public void deleteAnimal(Long id) {
        Animal animal = findByIdOrThrow(id, animalRepository);
        animalRepository.delete(animal);
    }

    @Override
    public List<AnimalResponseDto> getAllAnimals() {
        return animalRepository.findAll().stream()
                .map(mapper::mapToDto)
                .collect(Collectors.toList());
    }
}
