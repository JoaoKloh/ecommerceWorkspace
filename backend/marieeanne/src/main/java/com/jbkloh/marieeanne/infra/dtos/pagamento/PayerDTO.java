package com.jbkloh.marieeanne.infra.dtos.pagamento;
public record PayerDTO(
    String email,
    String firstName,
    String lastName,
    IdentificationDTO identification
) {
}
