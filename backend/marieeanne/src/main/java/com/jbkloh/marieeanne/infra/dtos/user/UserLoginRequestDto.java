package com.jbkloh.marieeanne.infra.dtos.user;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record UserLoginRequestDto(
    @Email(message = "O email deve ser válido")
    String email,
    @NotBlank(message = "A senha não pode ser vazia")
    String password
) {
    
}