package com.jbkloh.marieeanne.infra.dtos.user;

public record UserLoginResponseDTO(
     String jwtToken,
     String refreshToken,
     String email
){}
