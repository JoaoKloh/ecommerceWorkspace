package com.jbkloh.marieeanne.infra.dtos.cliente;

import java.time.LocalDate;

import com.jbkloh.marieeanne.infra.dtos.endereco.EnderecoResponseDto;
public record ClienteResponseDTO(
    Long id,
    String nome,
    String telefone,
    EnderecoResponseDto endereco,
    LocalDate dataNascimento
) {
    
}