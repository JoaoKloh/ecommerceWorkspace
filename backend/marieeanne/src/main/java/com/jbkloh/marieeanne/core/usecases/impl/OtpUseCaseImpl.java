package  com.jbkloh.marieeanne.core.usecases.impl;
import java.security.SecureRandom;

import com.jbkloh.marieeanne.core.ports.OptRepositoryPort;
import com.jbkloh.marieeanne.core.usecases.OtpUseCase;

public class OtpUseCaseImpl implements OtpUseCase {

    private final SecureRandom secureRandom = new SecureRandom();
    private final OptRepositoryPort optRepositoryPort;

    public OtpUseCaseImpl(OptRepositoryPort optRepositoryPort) {
        this.optRepositoryPort = optRepositoryPort;
    }
    
    @Override
    public String gerarCodigoOtp(String email) {
        validarEmail(email);
        int num = 100000 + secureRandom.nextInt(900000);
        String codigo= String.valueOf(num);
        this.optRepositoryPort.armazenarCodigo(email, codigo);
        return codigo;
    }

    @Override
    public String validarCodigoOpt(String codigo, String email) {
        validarEmail(email);
        String codigoArmazenado = optRepositoryPort.getCodigo(email);
        if(codigoArmazenado==null||!codigoArmazenado.equals(codigo)){
            throw new IllegalArgumentException("O codigo de autenticação está incorreto, favor realizar o login novamente.");
        }
        else{
            optRepositoryPort.removerCodigo(email);
            return "Acesso Liberado";
        }
    }

    private void validarEmail(String email) {
            if (email == null || email.trim().isEmpty()) {
                throw new IllegalArgumentException("O email não pode ser nulo.");
            }
        }
    
}
