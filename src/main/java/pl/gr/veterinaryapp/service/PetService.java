package pl.gr.veterinaryapp.service;

import org.springframework.security.core.userdetails.User;
import pl.gr.veterinaryapp.model.dto.PetRequestDto;
import pl.gr.veterinaryapp.model.dto.PetResponseDto;

import java.util.List;

public interface PetService {

    PetResponseDto getPetById(User user, Long id);
    PetResponseDto createPet(User user, PetRequestDto petRequestDTO);
    void deletePet(Long id);
    List<PetResponseDto> getAllPets(User user);
}
