package pl.gr.veterinaryapp.service;

import pl.gr.veterinaryapp.model.dto.UserDto;
import pl.gr.veterinaryapp.model.dto.VetAppUserResponseDto;
import pl.gr.veterinaryapp.model.entity.VetAppUser;

import java.util.List;

public interface UserService {

    List<VetAppUserResponseDto> getAllUsers();

    VetAppUserResponseDto getUser(Long id);

    VetAppUserResponseDto createUser(UserDto user);

    void deleteUser(Long id);
}
