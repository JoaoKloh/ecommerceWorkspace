package com.jbkloh.marieeanne.infra.dtos.itemCarrinho;

import java.math.BigDecimal;

public record itemCarrinhoResponseDTO(
    Long produtoId,
    String nomeProduto,
    int quantidade,
    BigDecimal precoUnitario,
    BigDecimal subtotal
) {}
