package com.jbkloh.marieeanne.infra.models;


import java.math.BigDecimal;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@Entity
@Setter
@Getter
@NoArgsConstructor
@RequiredArgsConstructor
@Table(name = "produto")
public class ProdutoEntity {
    
    @jakarta.persistence.Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    @Column(name="produto_id",unique=true)
    private Long id;

    @Column(name = "nome", length = 100, nullable = false)
    @NonNull
    private String nome;

    @Column(name = "valor", nullable = false)
    @NonNull
    private BigDecimal preco;

    @Column(name = "descricao", length = 255, nullable = false)
    @NonNull
    private String descricao;

    @Column(name = "categoria", length = 50, nullable = false)
    @NonNull
    private String categoria;

    @Column(name = "imagem_url", length = 255, nullable = false)
    @NonNull
    private String imagemUrl;

    @Column(name="desconto_percentual", nullable = true)
    private BigDecimal desconto;

    @Column(name = "esta_disponivel",nullable = false)
    private Boolean estaDisponivel;
}
