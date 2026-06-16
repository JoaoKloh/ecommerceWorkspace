package com.jbkloh.marieeanne.core.models;

import java.math.BigDecimal;

public class ItemPedido {

    private Long id;
    private Pedido pedido;
    private BigDecimal precoUnitario;
    private ProdutoLoja produto;
    private Integer quantidade;

    public ItemPedido( ProdutoLoja produto, Integer quantidade, BigDecimal precoUnitario) {
        this.produto = produto;
        this.quantidade = quantidade;
        this.precoUnitario = precoUnitario;
    }
     public ItemPedido() {
   
    }

    public ItemPedido(Pedido pedido, BigDecimal precoUnitario, ProdutoLoja produto, Integer quantidade) {
        this.pedido = pedido;
        this.precoUnitario = precoUnitario;
        this.produto = produto;
        this.quantidade = quantidade;
    }

    public BigDecimal getPrecoUnitario() {
        return precoUnitario;
    }
    public void setPrecoUnitario(BigDecimal precoUnitario) {
        this.precoUnitario = precoUnitario;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Pedido getPedido() {
        return pedido;
    }

    public void setPedido(Pedido pedido) {
        this.pedido = pedido;
    }

    public ProdutoLoja getProduto() {
        return produto;
    }

    public void setProduto(ProdutoLoja produto) {
        this.produto = produto;
    }

    public Integer getQuantidade() {
        return quantidade;
    }

    public void setQuantidade(Integer quantidade) {
        this.quantidade = quantidade;
    }

}
