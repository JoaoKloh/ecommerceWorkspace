package com.jbkloh.marieeanne.infra.models;
import java.math.BigDecimal;
import java.util.List;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "carrinho")
public class CarrinhoEntity {

    @jakarta.persistence.Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    @Column(name="carrinho_id",unique=true)
    private Long id;

    @OneToOne(fetch=FetchType.EAGER)
    @JoinColumn(name = "user_id", referencedColumnName = "user_id")
    private UserEntity user;

    @OneToMany(mappedBy = "carrinho", cascade = jakarta.persistence.CascadeType.ALL, orphanRemoval = true)
    private List<ItemCarrinhoEntity> itens;

    @Column(name = "valor_total")
    private BigDecimal valorTotal;

    @OneToOne
    @JoinColumn(name = "pedido_id", referencedColumnName = "pedido_id")
    private PedidoEntity pedido;


    
}
