package com.jbkloh.marieeanne.infra.dtos.cliente;

import java.time.LocalDate;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record ClienteCheckoutDTO(
    @NotBlank(message = "O nome não pode estar em branco.")
    @Size(min = 3, max = 100, message = "O nome deve ter entre 3 e 100 caracteres.")
    String nome,

    @NotBlank(message = "O telefone é obrigatório.")
    @Pattern(regexp = "^\\d{10,11}$", message = "O telefone deve conter apenas números (10 ou 11 dígitos).")
    String telefone,
    
    @NotNull(message = "A data de nascimento é obrigatória.")
    @Past(message = "A data de nascimento deve ser uma data no passado.")
    LocalDate dataNascimento
) {
}
