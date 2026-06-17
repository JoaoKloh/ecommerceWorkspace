package com.jbkloh.marieeanne.core.models;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;

public class Carrinho {
    private List<ItemCarrinho> produtos = new ArrayList<>();    
    private Usuario cliente;
    private Long id;
    private BigDecimal valorTotal;

    public Carrinho(){
        
    }

    public void setValorTotal(BigDecimal valorTotal) {
        this.valorTotal = valorTotal;
    }

    public BigDecimal getValorTotal() {
        if (this.produtos == null || this.produtos.isEmpty()) {
            return BigDecimal.ZERO.setScale(2, RoundingMode.HALF_UP);
        }
        this.valorTotal= this.produtos.stream()
        .map(item -> item.getPrecoUnitario().multiply(BigDecimal.valueOf(item.getQuantidade())))
        .reduce(BigDecimal.ZERO, BigDecimal::add)
        .setScale(2, RoundingMode.HALF_UP);
        return valorTotal;
    }

    public Carrinho(Long id, BigDecimal valorTotal) {
        this.id = id;
        this.valorTotal = valorTotal;
    }


    public Carrinho(List<ItemCarrinho> produtos, Usuario cliente, Long id, BigDecimal valorTotal) {
        this.produtos = produtos;
        this.cliente = cliente;
        this.id = id;
        this.valorTotal = valorTotal;
    }
    
    public Pedido finalizarCompra(List<ItemPedido> produtos, Cliente cliente){
        if (produtos.isEmpty()) {
            throw new IllegalStateException("O carrinho está vazio. Adicione produtos antes de finalizar a compra.");
        }
        Pedido pedido = new Pedido(cliente, produtos);
        return pedido;
    }

    public List<ItemCarrinho> getProdutos() {
        return produtos;
    }
    public Usuario getCliente() {
        return cliente;
    }
    public void setId(Long id) {
        this.id = id;
    }
    public Long getId() {
        return id;
    }

    public void setCliente(Usuario cliente) {
        this.cliente = cliente;
    }

    public void setProdutos(List<ItemCarrinho> produtos) {
        this.produtos = produtos;
    }
}
