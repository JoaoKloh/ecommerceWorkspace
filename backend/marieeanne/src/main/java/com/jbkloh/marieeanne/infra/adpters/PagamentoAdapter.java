package com.jbkloh.marieeanne.infra.adpters;

import org.springframework.stereotype.Component;

import com.jbkloh.marieeanne.core.models.Pagamento;
import com.jbkloh.marieeanne.core.ports.PagamentoRepositoryPort;
import com.jbkloh.marieeanne.infra.mappers.PagamentoMapper;
import com.jbkloh.marieeanne.infra.persistence.PagamentoJpaRepository;

import lombok.AllArgsConstructor;

@Component  
@AllArgsConstructor
public class PagamentoAdapter implements PagamentoRepositoryPort {

    private final PagamentoJpaRepository pagamentoJpaRepository;
    private final PagamentoMapper pagamentoMapper;

    @Override
    public void save(Pagamento pagamento){
        pagamentoJpaRepository.save(pagamentoMapper.toEntity(pagamento));
    }
    
}
