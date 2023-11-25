package pl.gr.veterinaryapp.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pl.gr.veterinaryapp.exception.IncorrectDataException;
import pl.gr.veterinaryapp.exception.ResourceNotFoundException;
import pl.gr.veterinaryapp.mapper.VetMapper;
import pl.gr.veterinaryapp.model.dto.VetRequestDto;
import pl.gr.veterinaryapp.model.dto.VetResponseDto;
import pl.gr.veterinaryapp.model.entity.Vet;
import pl.gr.veterinaryapp.repository.VetRepository;
import pl.gr.veterinaryapp.service.VetService;
import pl.gr.veterinaryapp.service.validator.DataValidator;

import java.util.List;
import java.util.stream.Collectors;

import static pl.gr.veterinaryapp.service.validator.DataValidator.*;

@RequiredArgsConstructor
@Service
public class VetServiceImpl implements VetService {

    private final VetRepository vetRepository;
    private final VetMapper mapper;

    @Override
    public VetResponseDto getVetById(Long id) {
        Vet vet = findByIdOrThrow(id, vetRepository);
        return mapper.map(vet);
    }

    @Override
    public List<VetResponseDto> getAllVets() {
        return vetRepository.findAll().stream()
                .map(mapper::map)
                .collect(Collectors.toList());
    }

    @Transactional
    @Override
    public VetResponseDto createVet(VetRequestDto vetRequestDTO) {
        validateNameOrSurname(vetRequestDTO.getName(), vetRequestDTO.getSurname());
        Vet vet = mapper.map(vetRequestDTO);
        vetRepository.save(vet);
        return mapper.map(vet);
    }

    @Transactional
    @Override
    public void deleteVet(Long id) {
        Vet result = findByIdOrThrow(id, vetRepository);
        vetRepository.delete(result);
    }
}
