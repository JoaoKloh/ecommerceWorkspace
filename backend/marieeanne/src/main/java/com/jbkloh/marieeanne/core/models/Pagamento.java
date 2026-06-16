package com.jbkloh.marieeanne.core.models;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import com.jbkloh.marieeanne.core.models.enums.PagamentoStatus;

public class Pagamento {
    private Long id;

    private BigDecimal valor;

    private Integer parcelas;
    private Pedido pedido;
    private String transacaoIdMercadoPago;
    private PagamentoStatus statusPagamento;
    private LocalDateTime dataPagamento;

    public Pagamento() {
    }

    public Pagamento(LocalDateTime dataPagamento, Long id, Integer parcelas, PagamentoStatus statusPagamento, BigDecimal valor) {
        this.dataPagamento = dataPagamento;
        this.id = id;
        this.parcelas = parcelas;
        this.statusPagamento = statusPagamento;
        this.valor = valor;
    }
    public Pagamento(BigDecimal valor, PagamentoStatus statusPagamento) {
        this.valor = valor;
        this.statusPagamento = statusPagamento;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public BigDecimal getValor() {
        return valor;
    }

    public void setValor(BigDecimal valor) {
        this.valor = valor;
    }

    public Integer getParcelas() {
        return parcelas;
    }

    public void setParcelas(Integer parcelas) {
        this.parcelas = parcelas;
    }

    public PagamentoStatus getStatusPagamento() {
        return statusPagamento;
    }

    public void setStatusPagamento(PagamentoStatus statusPagamento) {
        this.statusPagamento = statusPagamento;
    }

    public LocalDateTime getDataPagamento() {
        return dataPagamento;
    }

    public void setDataPagamento(LocalDateTime dataPagamento) {
        this.dataPagamento = dataPagamento;
    }

    public Pedido getPedido() {
        return pedido;
    }

    public void setPedido(Pedido pedido) {
        this.pedido = pedido;
    }

    public String getTransacaoIdMercadoPago() {
        return transacaoIdMercadoPago;
    }

    public void setTransacaoIdMercadoPago(String transacaoIdMercadoPago) {
        this.transacaoIdMercadoPago = transacaoIdMercadoPago;
    }

    
}
