package com.jbkloh.marieeanne.core.usecases;

public interface OtpUseCase {

    String gerarCodigoOtp(String email);
    String validarCodigoOpt(String codigo, String email);
    
}
