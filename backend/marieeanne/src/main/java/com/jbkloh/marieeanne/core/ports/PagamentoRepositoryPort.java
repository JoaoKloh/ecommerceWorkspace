package com.jbkloh.marieeanne.core.ports;
import com.jbkloh.marieeanne.core.models.Pagamento;

public interface PagamentoRepositoryPort {

    void save(Pagamento pagamento);
    
}
