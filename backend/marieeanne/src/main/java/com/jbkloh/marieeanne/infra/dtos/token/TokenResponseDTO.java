package com.jbkloh.marieeanne.infra.dtos.token;

public record TokenResponseDTO(
    String accessToken,
    String refreshToken,
    String tokenType
) {
    
}
