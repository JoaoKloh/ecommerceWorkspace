package com.jbkloh.marieeanne.core.models;

public class ProdutoLoja{
    private Long id;
    private Produto produto;
    private Loja loja;
    private Integer quantidade;

    public ProdutoLoja(){

    }
    public ProdutoLoja(Produto produto, Loja loja, Integer quantidade) {
        this.produto = produto;
        this.loja = loja;
        this.quantidade = quantidade;
    }
    public Produto getProduto() {
        return produto;
    }
    public void setProduto(Produto produto) {
        this.produto = produto;
    }
    public Loja getLoja() {
        return loja;
    }
    public void setLoja(Loja loja) {
        this.loja = loja;
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


}