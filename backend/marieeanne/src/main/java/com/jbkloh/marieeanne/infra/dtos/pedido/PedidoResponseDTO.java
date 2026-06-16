package com.jbkloh.marieeanne.infra.dtos.pedido;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import com.jbkloh.marieeanne.infra.dtos.itemPedido.ItemPedidoResponseDTO;
import com.jbkloh.marieeanne.infra.dtos.pagamento.PagamentoResponseDTO;

public record PedidoResponseDTO(
    PagamentoResponseDTO pagamento,
    Integer quantidadeProdutos,
    List<ItemPedidoResponseDTO> produtos,
    LocalDateTime dataCriacao,
    BigDecimal valor,
    String status

) {

}