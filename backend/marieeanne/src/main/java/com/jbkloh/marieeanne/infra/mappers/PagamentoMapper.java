package com.jbkloh.marieeanne.infra.mappers;
import org.springframework.stereotype.Component;

import com.jbkloh.marieeanne.core.models.Pagamento;
import com.jbkloh.marieeanne.infra.models.PagamentoEntity;

@Component
public class PagamentoMapper {

    public PagamentoEntity toEntity(Pagamento pagamento){
        PagamentoEntity pagamentoEntity = new PagamentoEntity();
        pagamentoEntity.setId(pagamento.getId());
        pagamentoEntity.setValor(pagamento.getValor());
        pagamentoEntity.setStatusPagamento(pagamento.getStatusPagamento().name());
        return pagamentoEntity;
    }
    
}
