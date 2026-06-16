package com.jbkloh.marieeanne.infra.mappers;

import org.springframework.stereotype.Component;

import com.jbkloh.marieeanne.core.models.Loja;
import com.jbkloh.marieeanne.infra.models.LojaEntity;

@Component
public class LojaMapper {

    public static Loja toDomain(LojaEntity entity){
        Loja domain = new Loja();
        domain.setEndereco(EnderecoMapper.toDomain(entity.getEndereco()));
        domain.setId(entity.getId());
        domain.setNome(entity.getNome());

        return domain;
    }

    public static LojaEntity toEntity(Loja loja){
        LojaEntity entity = new LojaEntity();
        entity.setId(loja.getId());
        entity.setNome(loja.getNome());
        entity.setEndereco(EnderecoMapper.toEntity(loja.getEndereco()));
        
        return entity;
    }
    
}
