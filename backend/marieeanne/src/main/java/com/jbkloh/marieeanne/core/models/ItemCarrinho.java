package com.jbkloh.marieeanne.core.models;

import java.math.BigDecimal;

public class ItemCarrinho {

    private Long id;
    private Carrinho carrinho;
    private ProdutoLoja produto;
    private Integer quantidade;
    private BigDecimal precoUnitario;

    public ItemCarrinho(Long id, ProdutoLoja produto, Integer quantidade,BigDecimal precoUnitario) {
        this.id = id;
        this.precoUnitario = precoUnitario;
        this.produto = produto;
        this.quantidade = quantidade;
    }


    public Carrinho getCarrinho() {
        return carrinho;
    }

    public void setCarrinho(Carrinho carrinho) {
        this.carrinho = carrinho;
    }
   

    public BigDecimal getPrecoUnitario() {
        if (this.produto==null) return BigDecimal.ZERO;
            precoUnitario=this.produto.getProduto().getPrecoComDesconto();
        return precoUnitario;
    }

    public void setPrecoUnitario(BigDecimal precoUnitario) {
    }

    public ProdutoLoja getProduto() {
        return produto;
    }

    public void setProduto(ProdutoLoja produto) {
        this.produto = produto;
    }

    public ItemCarrinho() {
    }

    public Integer getQuantidade() {
        return quantidade;
    }

    public void setQuantidade(Integer quantidade) {
        this.quantidade = quantidade;
    }
    public void setId(Long id) {
        this.id = id;
    }

    public Long getId() {
        return id;
    }

    public ItemCarrinho(Carrinho carrinho, ProdutoLoja produto, Integer quantidade) {
        this.carrinho = carrinho;
        this.produto = produto;
        this.quantidade = quantidade;
    }

    public ItemCarrinho(Long id, BigDecimal precoUnitario, Integer quantidade) {
        this.id = id;
        this.quantidade = quantidade;
    }

    public ItemCarrinho(ProdutoLoja produto, Integer quantidade,BigDecimal precoUnitario) {
        this.produto = produto;
        this.quantidade = quantidade;
        this.precoUnitario = precoUnitario;
    } 

    
}
