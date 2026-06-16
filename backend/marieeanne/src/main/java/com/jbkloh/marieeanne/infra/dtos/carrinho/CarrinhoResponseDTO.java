package com.jbkloh.marieeanne.infra.dtos.carrinho;

import java.math.BigDecimal;
import java.util.List;

import com.jbkloh.marieeanne.infra.dtos.itemCarrinho.itemCarrinhoResponseDTO;

public record CarrinhoResponseDTO(
    List<itemCarrinhoResponseDTO> itens,
    BigDecimal valorTotalItens
) {
    
}
