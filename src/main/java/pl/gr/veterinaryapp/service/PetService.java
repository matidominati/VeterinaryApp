package pl.gr.veterinaryapp.service;

import org.springframework.security.core.userdetails.User;
import pl.gr.veterinaryapp.model.dto.PetRequestDto;
import pl.gr.veterinaryapp.model.dto.PetResponseDto;
import pl.gr.veterinaryapp.model.dto.UserDto;
import pl.gr.veterinaryapp.model.entity.Pet;

import java.util.List;

public interface PetService {

    PetResponseDto getPetById(UserDto user, Long id);

    PetResponseDto createPet(UserDto user, PetRequestDto petRequestDTO);

    void deletePet(Long id);

    List<PetResponseDto> getAllPets(UserDto user);
}
