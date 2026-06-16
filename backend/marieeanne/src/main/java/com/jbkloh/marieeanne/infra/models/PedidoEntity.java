package com.jbkloh.marieeanne.infra.models;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.time.LocalTime;
import java.time.LocalDate;

import org.springframework.data.annotation.CreatedDate;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
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
@NoArgsConstructor
@AllArgsConstructor
@Table(name= "pedido")
public class PedidoEntity {

    @jakarta.persistence.Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    @Column(name="pedido_id",unique=true)
    private Long id;

    @Column(name = "status", length = 20, nullable = false)
    private String status;

    @Column(name = "valor_total", nullable = false)
    private BigDecimal valorTotal;

    @ManyToOne
    @JoinColumn(name= "cliente_id", referencedColumnName = "cliente_id")
    private ClienteEntity cliente;

    @OneToOne
    @JoinColumn(name="carrinho_id", referencedColumnName = "carrinho_id")
    private CarrinhoEntity carrinho;

    @OneToMany(mappedBy = "pedido", cascade=CascadeType.ALL,orphanRemoval=true)
    private List<ItemPedidoEntity> produtos;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "pagamento_id")
    private PagamentoEntity pagamento;

    @Column(name = "data_criacao", updatable= false)
    @CreatedDate
    private LocalDateTime dataCriacao;

    @Column(name = "data_atualizacao", nullable = false)
    @CreatedDate
    private LocalDateTime dataAtualizacao;

    @Column(name = "data_retirada", nullable = false)
    private LocalDate dataRetirada; 
    
    @Column(name = "hora_retirada", nullable = false)
    private LocalTime horaRetirada; 

    @ManyToOne
    @JoinColumn(name = "endereco_id", referencedColumnName = "endereco_id", nullable = false)
    private EnderecoEntity endereco;

    @ManyToOne
    @JoinColumn(name = "loja_id", nullable = false) 
    private LojaEntity loja;


    public PedidoEntity(Long id, String status, BigDecimal valorTotal){
        this.id = id;
        this.status = status;
        this.valorTotal = valorTotal;
    }

    
}
