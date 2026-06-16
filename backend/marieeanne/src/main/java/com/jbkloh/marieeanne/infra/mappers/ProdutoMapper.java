package com.jbkloh.marieeanne.infra.mappers;

import com.jbkloh.marieeanne.core.models.Produto;
import com.jbkloh.marieeanne.infra.models.ProdutoEntity;

import lombok.experimental.UtilityClass;

@UtilityClass
public class ProdutoMapper {

    /**
     * Converte o Modelo de Domínio (Core) para Entidade JPA (Infra)
     */
    public  ProdutoEntity toEntity(Produto produto) {
        if (produto == null) return null;

        ProdutoEntity entity = new ProdutoEntity();
        entity.setId(produto.getId());
        entity.setNome(produto.getNome());
        entity.setDescricao(produto.getDescricao());
        entity.setCategoria(produto.getCategoria());
        entity.setImagemUrl(produto.getImagem());
        entity.setPreco(produto.getPreco());
        entity.setDesconto(produto.getDesconto());
        entity.setEstaDisponivel(produto.getEstaDisponivel());

        return entity;
    }

    /**
     * Converte a Entidade JPA (Infra) para o Modelo de Domínio (Core)
     */
    public  Produto toDomain(ProdutoEntity entity) {
        if (entity == null) return null;

        return new Produto(
            entity.getId(),
            entity.getNome(),
            entity.getDescricao(),
            entity.getCategoria(),
            entity.getImagemUrl(),
            entity.getPreco(),
            entity.getDesconto(),
            entity.getEstaDisponivel()
        );
    }
}