package com.jbkloh.marieeanne.infra.dtos.endereco;

import jakarta.validation.constraints.NotBlank;

public record EnderecoRequestDto(
    @NotBlank(message = "É necessário preencher o campo rua.")
    String rua,
    @NotBlank(message = "É necessário preencher o campo número.")
    String numero,
    String complemento,
    @NotBlank(message = "É necessário preencher o campo bairro.")
    String bairro,
    @NotBlank(message = "É necessário preencher o campo cidade.")   
    String cidade,
    @NotBlank(message = "É necessário preencher o campo estado.")
    String estado,
    @NotBlank(message = "É necessário preencher o campo cep.")
    String cep
) {
    
}
