package com.jbkloh.marieeanne.core.models;

import java.time.LocalDate;

public class Cliente {
    private Long id;
    private Usuario user;
    private String nome;
    private String telefone;
    private String cpf;
    private Endereco endereco;
    private LocalDate dataNascimento;

    public Cliente(
        Long id, 
        String nome, 
        String cpf,
        String telefone, 
        Endereco endereco, 
        LocalDate dataNascimento,
        Usuario user) {
        
        this.id = id;
        this.nome = nome;
        this.telefone = telefone;
        this.endereco = endereco;
        this.dataNascimento = dataNascimento;
        this.user = user;
        this.cpf = cpf;
    }

    public Cliente(
        Long id, 
        String nome, 
        String telefone, 
        Endereco endereco, 
        LocalDate dataNascimento) {
        
        this.id = id;
        this.nome = nome;
        this.telefone = telefone;
        this.endereco = endereco;
        this.dataNascimento = dataNascimento;
    }

    public Cliente(String nome,String cpf,String telefone,Endereco endereco, LocalDate dataNascimento) {
        this.nome = nome;
        this.telefone = telefone;
        this.endereco = endereco;
        this.dataNascimento = dataNascimento;
        this.cpf = cpf;
    }
    public Cliente(){
        
    }

    public Long getId() {
        return id;
    }

    public void setUser(Usuario user) {
        this.user = user;
    }
    public String getNome() {
        return nome;
    }

    public String getTelefone() {
        return telefone;
    }

    public Endereco getEndereco() {
        return endereco;
    }

    public LocalDate getDataNascimento() {
        return dataNascimento;
    }

    public String getEmailUsuario() {
        return user.getEmail();
    }

    public Usuario getUser() {
        return user;
    }

    public void setId(Long id) {
        this.id = id;
    }
    public void setNome(String nome) {
        this.nome = nome;
    }
    public void setTelefone(String telefone) {
        this.telefone = telefone;
    }
    public void setEndereco(Endereco endereco) {
        this.endereco = endereco;   
    }
    public void setDataNascimento(LocalDate dataNascimento) {
        this.dataNascimento = dataNascimento;
    }
    public String getCpf() {
        return cpf;
    }

    public void setCpf(String cpf) {
        this.cpf = cpf;
    }
}
    


