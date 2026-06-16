package com.jbkloh.marieeanne.core.models;

public class Loja {
    private Long id;
    private Endereco endereco;
    private String nome;

    public Loja(){

    }
    public Loja(Endereco endereco, String nome){
        this.endereco = endereco;
        this.nome = nome;
    }

    public Endereco getEndereco() {
        return endereco;
    }
    public void setEndereco(Endereco endereco) {
        this.endereco = endereco;
    }

    public Long getId() {
        return id;
    }
    public void setId(Long id) {
        this.id = id;
    }

    public String getNome() {
        return nome;
    }
    public void setNome(String nome) {
        this.nome = nome;
    }
}
