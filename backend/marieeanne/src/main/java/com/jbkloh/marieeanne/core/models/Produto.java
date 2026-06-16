package com.jbkloh.marieeanne.core.models;

import java.math.BigDecimal;
import java.math.RoundingMode;

public class Produto{
    private Long id;
    private String nome;
    private String descricao;
    private String categoria;
    private String imagem;
    private BigDecimal preco;
    private BigDecimal desconto;
    private Boolean estaDisponivel;

    public Produto(Long id, String nome, String descricao, String categoria, String imagem, BigDecimal preco,
            BigDecimal desconto, Boolean estaDisponivel) {
        this.id = id;
        this.nome = nome;
        this.descricao = descricao;
        this.categoria = categoria;
        this.imagem = imagem;
        this.preco = preco;
        this.desconto = desconto;
        this.estaDisponivel = estaDisponivel;
    }

    public Produto(String categoria, BigDecimal desconto, String descricao, Boolean estaDisponivel, String nome, BigDecimal preco) {
        this.categoria = categoria;
        this.desconto = desconto;
        this.descricao = descricao;
        this.estaDisponivel = estaDisponivel;
        this.nome = nome;
        this.preco = preco;
    }

    public Boolean getEstaDisponivel() {
        return estaDisponivel;
    }

    public void setImagem(String imagem) {
        this.imagem = imagem;
    }

    public void setEstaDisponivel(Boolean estaDisponivel) {
        this.estaDisponivel = estaDisponivel;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public Produto(){}

    public Long getId() {
        return id;
    }   

    public String getNome() {
        return nome;
    }

    public String getDescricao() {
        return descricao;
    }

    public String getCategoria() {
        return categoria;
    }

    public BigDecimal getDesconto() {
        return desconto;
    }

    public String getImagem() {
        return imagem;
    }

    public BigDecimal getPreco(){
        return this.preco;
    }
    public BigDecimal getPrecoComDesconto() {
    if (this.desconto == null || this.desconto.compareTo(BigDecimal.ZERO) <= 0) {
        return this.preco;
    }

    BigDecimal percentual = this.desconto.divide(BigDecimal.valueOf(100.00));
    BigDecimal fator = BigDecimal.ONE.subtract(percentual);
    
    return this.preco.multiply(fator).setScale(2, RoundingMode.HALF_UP);
}

    public void setCategoria(String categoria) {
        this.categoria = categoria;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public void setPreco(BigDecimal preco) {
        this.preco = preco;
    }

    public void setDesconto(BigDecimal desconto) {
        this.desconto = desconto;
    }
}