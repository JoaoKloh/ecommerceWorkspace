package com.jbkloh.marieeanne.infra.mappers;


import org.springframework.stereotype.Component;

import com.jbkloh.marieeanne.core.models.ProdutoLoja;
import com.jbkloh.marieeanne.infra.models.ProdutoLojaEntity;

import lombok.AllArgsConstructor;

@Component
@AllArgsConstructor
public class ProdutoLojaMapper {

    public static ProdutoLoja toDomain(ProdutoLojaEntity entity){
        ProdutoLoja produtoLoja = new ProdutoLoja();
        produtoLoja.setId(entity.getId());
        produtoLoja.setLoja(LojaMapper.toDomain(entity.getLoja()));
        produtoLoja.setProduto(ProdutoMapper.toDomain(entity.getProduto()));
        produtoLoja.setQuantidade(entity.getQuantidade());
        
        return produtoLoja;
    }
    public static ProdutoLojaEntity toEntity(ProdutoLoja produtoLoja){
        ProdutoLojaEntity entity = new ProdutoLojaEntity();
        entity.setId(produtoLoja.getId());
        entity.setLoja(LojaMapper.toEntity(produtoLoja.getLoja()));
        entity.setProduto(ProdutoMapper.toEntity(produtoLoja.getProduto()));
        entity.setQuantidade(produtoLoja.getQuantidade());

        return entity;
    }
}
