package com.jbkloh.marieeanne.infra.dtos.produto;

import java.math.BigDecimal;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

public record ProdutoRequestDTO(
    @NotBlank(message = "O nome do produto é obrigatório")
    @Size(min = 3, max = 100, message = "O nome deve ter entre 3 e 100 caracteres")
    String nome,

    @NotBlank(message = "A descrição é obrigatória")
    String descricao,

    @NotNull(message = "O preço é obrigatório")
    @Positive(message = "O preço deve ser maior que zero")
    BigDecimal preco,

    @NotBlank(message = "A categoria é obrigatória")
    String categoria,

    @NotNull(message = "A quantidade em estoque é obrigatória")
    @Positive(message = "O estoque não pode ser negativo")
    Integer estoque,

    BigDecimal desconto,
    @NotNull(message="É preciso informar a disponibilidade do produto.")
    Boolean estaDisponivel
) {}