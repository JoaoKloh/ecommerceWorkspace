package com.jbkloh.marieeanne.infra.dtos.token;

import jakarta.validation.constraints.NotBlank;

public record TokenRequestDTO(
    @NotBlank(message = "O Refresh Token é obrigatório.")
    String refreshToken
) {
    
}
