package com.jbkloh.marieeanne.infra.dtos.user;

import java.util.List;


public record LoginResponseDTO(
    String email,
    List<String> authoritities
) {
    
}
