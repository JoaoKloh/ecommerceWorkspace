package com.jbkloh.marieeanne.infra.dtos.token;
import jakarta.validation.constraints.Email;

public record OtpTokenRequestDTO(

    @Email(message = "O email inserido deve ser válido.")
    String email
) {
    
}
