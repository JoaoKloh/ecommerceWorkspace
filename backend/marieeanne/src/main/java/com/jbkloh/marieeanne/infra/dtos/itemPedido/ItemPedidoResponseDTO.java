package com.jbkloh.marieeanne.infra.dtos.itemPedido;

import java.math.BigDecimal;

public record ItemPedidoResponseDTO(
    Long id,
    Long productId,
    String productName,
    Integer quantity,
    BigDecimal precoUnitario
) {
    

}
