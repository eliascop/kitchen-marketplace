package br.com.kitchen.api.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class AuthRequestDTO {

    @NotBlank(message = "User cant be empty")
    private String login;

    @NotBlank(message = "Password cant be empty")
    private String password;

}
