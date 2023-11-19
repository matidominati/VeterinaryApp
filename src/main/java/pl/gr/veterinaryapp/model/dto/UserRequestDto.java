package pl.gr.veterinaryapp.model.dto;

import lombok.Data;

@Data
public class UserRequestDto {

    private String username;
    private String password;
    private Long role;
}
