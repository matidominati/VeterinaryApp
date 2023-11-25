package pl.gr.veterinaryapp.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pl.gr.veterinaryapp.exception.IncorrectDataException;
import pl.gr.veterinaryapp.exception.ResourceNotFoundException;
import pl.gr.veterinaryapp.mapper.AnimalMapper;
import pl.gr.veterinaryapp.model.dto.AnimalRequestDto;
import pl.gr.veterinaryapp.model.entity.Animal;
import pl.gr.veterinaryapp.repository.AnimalRepository;
import pl.gr.veterinaryapp.service.AnimalService;

import java.util.List;

@RequiredArgsConstructor
@Service
@Slf4j
public class AnimalServiceImpl implements AnimalService {

    private final AnimalRepository animalRepository;
    private final AnimalMapper mapper;

    @Override
    public Animal getAnimalById(long id) {
        log.info("Search process for an animal with ID: {} has started", id);
        return animalRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Wrong id."));
    }

    @Transactional
    @Override
    public Animal createAnimal(AnimalRequestDto animalRequestDto) {
        var animal = animalRepository.findBySpecies(animalRequestDto.getSpecies());
        if (animal.isPresent()) {
            throw new IncorrectDataException("Species exists.");
        }
        log.info("New animal with species: {} has been created", animalRequestDto.getSpecies());
        return animalRepository.save(mapper.map(animalRequestDto));
    }

    @Transactional
    @Override
    public void deleteAnimal(long id) {
        log.info("Process of searching for an animal with ID: {} has started", id);
        Animal animal = animalRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Wrong id."));
        animalRepository.delete(animal);
        log.info("Animal with ID {} removed", id);
    }

    @Override
    public List<Animal> getAllAnimals() {
        return animalRepository.findAll();
    }
}
