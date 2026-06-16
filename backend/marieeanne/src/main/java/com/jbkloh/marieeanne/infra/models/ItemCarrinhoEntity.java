package com.jbkloh.marieeanne.infra.models;

import java.math.BigDecimal;

import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.Setter;

@Entity
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "item_carrinho")
public class ItemCarrinhoEntity {

    @Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    @Column(name="itemcarrinho_id",unique=true)
    private Long id;
    
    @ManyToOne
    @JoinColumn(name = "carrinho_id", referencedColumnName = "carrinho_id",nullable = false)
    @JsonIgnore
    private CarrinhoEntity carrinho;

    @NonNull
    @ManyToOne
    @JoinColumn(name = "produto_loja_id", referencedColumnName = "produto_loja_id", nullable = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    private ProdutoLojaEntity produto;

    @Column(name = "quantidade", nullable = false)
    private Integer quantidade;

    @Column(name = "preco_item")
    private BigDecimal precoUnitario;

    public ItemCarrinhoEntity(Long id, BigDecimal precoUnitario, Integer quantidade){
        this.id = id;
        this.precoUnitario = precoUnitario;
        this.quantidade = quantidade;
    }
}
