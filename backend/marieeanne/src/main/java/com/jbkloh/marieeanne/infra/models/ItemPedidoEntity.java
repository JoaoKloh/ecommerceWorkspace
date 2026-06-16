package com.jbkloh.marieeanne.infra.models;

import java.math.BigDecimal;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@Entity
@Setter
@Getter
@Table(name = "item_pedido")
@NoArgsConstructor
@RequiredArgsConstructor
public class ItemPedidoEntity {
    
    @Id
    @GeneratedValue(strategy = jakarta.persistence.GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "pedido_id", referencedColumnName = "pedido_id", nullable = false)
    @NonNull
    private PedidoEntity pedido;

    @ManyToOne
    @JoinColumn(name = "produto_loja_id", referencedColumnName = "produto_loja_id", nullable = false)
    @NonNull
    private ProdutoLojaEntity produto;

    @Column(name = "quantidade", nullable = false)
    @NonNull
    private Integer quantidade;

    @Column(name="preco_item")
    private BigDecimal preco;

}
