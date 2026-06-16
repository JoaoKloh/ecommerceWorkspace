package com.jbkloh.marieeanne.infra.dtos.pagamento;

import java.math.BigDecimal;

public record PaymentRequestDTO(
    BigDecimal transactionAmount,
    String description,
    String paymentMethodId,
    String token,
    Integer installments,
    String issuerId,
    PayerDTO payer
) {
}
