package com.jbkloh.marieeanne.infra.models;

import java.time.LocalDate;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Setter
@Getter
@NoArgsConstructor
@Table(name = "cliente")
public class ClienteEntity {
    
    @jakarta.persistence.Id
    @GeneratedValue(strategy=GenerationType.IDENTITY) 
    @Column(name="cliente_id",unique=true)
    private Long id;

    @Column(name = "nome", length = 100, nullable = false)
    private String nome;

    @Column(name = "telefone", length = 20, nullable = true)
    private String telefone;

    @Column(name="cpf",length=11,nullable=false)
    private String cpf;

    @OneToOne(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "endereco_id", referencedColumnName = "endereco_id", nullable = false)
    private EnderecoEntity endereco;

    @Column(name = "data_nascimento", nullable = true)
    private LocalDate dataNascimento;

    @OneToOne
    @JoinColumn(name = "user_id")
    private UserEntity usuario;


}
