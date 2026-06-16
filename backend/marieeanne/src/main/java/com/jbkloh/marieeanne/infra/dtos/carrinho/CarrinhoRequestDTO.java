package com.jbkloh.marieeanne.infra.dtos.carrinho;


import com.jbkloh.marieeanne.core.models.ItemCarrinho;

import jakarta.validation.constraints.NotNull;

public record CarrinhoRequestDTO(

    @NotNull(message = "É necessário selecionar um produto para ser adicionado ao carrinho.")
    Long idItem,

    @NotNull(message = "Insira a quantidade do produto.")
    Integer quantidade

    

){
    public int getEstoque(){
        return new ItemCarrinho().getQuantidade();
    }

}