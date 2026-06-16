package com.jbkloh.marieeanne.infra.dtos.pedido;

import java.math.BigDecimal;

public record PedidoCreateResponseDTO(
    Long pedidoId,
    String preferenceId,
    BigDecimal amount
) {
}
