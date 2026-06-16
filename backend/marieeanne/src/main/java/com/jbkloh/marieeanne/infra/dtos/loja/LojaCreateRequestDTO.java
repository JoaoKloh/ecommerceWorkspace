package com.jbkloh.marieeanne.infra.dtos.loja;

import com.jbkloh.marieeanne.infra.dtos.endereco.EnderecoRequestDto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record LojaCreateRequestDTO(
    @NotBlank(message = "O nome da loja é obrigatório.")
    @Size(max = 100, message = "O nome da loja não pode passar de 100 caracteres.")
    String nome,

    @NotNull(message = "Os dados de endereço são obrigatórios.")
    @Valid
    EnderecoRequestDto endereco
) {
}