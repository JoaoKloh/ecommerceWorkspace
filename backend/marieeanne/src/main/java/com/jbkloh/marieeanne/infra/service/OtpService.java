package com.jbkloh.marieeanne.infra.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.jbkloh.marieeanne.core.usecases.OtpUseCase;
@Service
public class OtpService {

    @Autowired
    private OtpUseCase otpUseCase;

    public String gerarCodigoValido(String email){
        return otpUseCase.gerarCodigoOtp(email);
    }

    public String validarCodigoOtp(String codigo, String email){
        return otpUseCase.validarCodigoOpt(codigo, email);
    }
}
