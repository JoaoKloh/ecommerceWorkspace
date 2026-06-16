package com.jbkloh.marieeanne.infra.dtos.pagamento;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record PagamentoResponseDTO(
    Long id,
    BigDecimal valor,
    Integer parcelas,
    String statusPagamento,
    LocalDateTime dataPagamento
) {
    
}
