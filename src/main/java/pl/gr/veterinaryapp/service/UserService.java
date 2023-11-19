package pl.gr.veterinaryapp.service;

import pl.gr.veterinaryapp.model.dto.UserRequestDto;
import pl.gr.veterinaryapp.model.dto.UserResponseDto;

import java.util.List;

public interface UserService {

    List<UserResponseDto> getAllUsers();
    UserResponseDto getUser(Long id);
    UserResponseDto createUser(UserRequestDto user);
    void deleteUser(Long id);
}
