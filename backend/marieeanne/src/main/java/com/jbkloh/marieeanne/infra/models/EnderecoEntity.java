package com.jbkloh.marieeanne.infra.models;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "endereco")
@Setter
@Getter
@AllArgsConstructor
@RequiredArgsConstructor
public class EnderecoEntity {

    @jakarta.persistence.Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    @Column(name="endereco_id",unique=true)
    private Long id;

    @Column(name = "rua")
    private String rua;

    @Column(name = "numero")
    private String numero;

    @Column(name = "complemento")
    private String complemento;
    
    @Column(name = "bairro")
    private String bairro;

    @Column(name = "cidade")
    private String cidade;

    @Column(name = "estado")
    private String estado;

    @Column(name = "cep")
    private String cep;
    
}
