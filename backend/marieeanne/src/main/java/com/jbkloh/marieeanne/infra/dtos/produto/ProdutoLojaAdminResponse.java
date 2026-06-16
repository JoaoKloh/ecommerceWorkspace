package com.jbkloh.marieeanne.infra.dtos.produto;

import java.math.BigDecimal;

public record ProdutoLojaAdminResponse(
    Long id,
    String nome,
    String imagem,
    String categoria,
    Integer estoque,
    BigDecimal preco,
    Boolean estaDisponivel
) {
    
}
