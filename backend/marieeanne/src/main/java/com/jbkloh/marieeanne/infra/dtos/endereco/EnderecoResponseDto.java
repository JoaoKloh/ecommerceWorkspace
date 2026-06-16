package com.jbkloh.marieeanne.infra.dtos.endereco;
public record EnderecoResponseDto(
    Long id,
    String rua,
    String numero,
    String complemento,
    String bairro,
    String cidade,
    String estado,
    String cep
) {
    
}
