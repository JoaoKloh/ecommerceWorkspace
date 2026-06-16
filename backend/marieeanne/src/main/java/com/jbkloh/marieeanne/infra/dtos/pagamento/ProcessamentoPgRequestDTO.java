package com.jbkloh.marieeanne.infra.dtos.pagamento;

public record ProcessamentoPgRequestDTO(
    Long idPedido,
    String token,
    String description,
    String paymentMethodId,
    Integer installments) {}
