package com.jbkloh.marieeanne.infra.dtos.produto;

import java.math.BigDecimal;

import com.jbkloh.marieeanne.infra.dtos.endereco.EnderecoResponseDto;

public record ProdutoCardapioResponse(
    Long id,
    String nome,
    String descricao,
    String categoria,
    String imagem,
    Integer estoque,
    BigDecimal preco,
    BigDecimal desconto,
    BigDecimal precoDesconto,
    Boolean estaDisponivel,
    EnderecoResponseDto endereco
) {}