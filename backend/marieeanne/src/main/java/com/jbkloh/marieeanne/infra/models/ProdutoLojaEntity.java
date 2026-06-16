package com.jbkloh.marieeanne.infra.models;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(
    name = "produtos_estoque_lojas",
    uniqueConstraints = {
        @UniqueConstraint(columnNames = {"produto_id", "loja_id"}) 
    } 
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ProdutoLojaEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "produto_loja_id")
    private Long id;

    @ManyToOne(cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @JoinColumn(name = "produto_id", nullable = false)
    private ProdutoEntity produto; 

    @ManyToOne
    @JoinColumn(name = "loja_id", nullable = false)
    private LojaEntity loja; 

    @Column(name = "quantidade", nullable = false)
    private Integer quantidade; 
    
}