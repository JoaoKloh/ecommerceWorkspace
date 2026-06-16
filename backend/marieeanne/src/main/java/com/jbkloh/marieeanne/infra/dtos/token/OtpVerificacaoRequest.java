package com.jbkloh.marieeanne.infra.dtos.token;

public record OtpVerificacaoRequest(
    String email,
    String codigo
) {
    
}
