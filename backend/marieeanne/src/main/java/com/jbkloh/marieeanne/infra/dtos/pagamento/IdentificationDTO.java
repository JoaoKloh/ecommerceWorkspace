package com.jbkloh.marieeanne.infra.dtos.pagamento;


public record IdentificationDTO(
    String type, // "CPF" ou "CNPJ"
    String number // Número do CPF ou CNPJ
) {
}
