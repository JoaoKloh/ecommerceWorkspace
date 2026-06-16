package com.jbkloh.marieeanne.core.models;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.time.LocalTime;
import java.time.LocalDate;

import com.jbkloh.marieeanne.core.models.enums.PedidoStatus;


public class Pedido {
    private Cliente cliente;
    private Long id;
    private List<ItemPedido> produtos;
    private PedidoStatus status;
    private Integer quantidadeProdutos;
    private LocalDateTime dataCriacao;
    private LocalDateTime dataAtualizacao;
    private LocalDate dataRetirada;
    private LocalTime horaRetirada;
    private BigDecimal valor;
    private Pagamento pagamento;
    private Endereco endereco;
    private Loja loja;

    public Pedido( PedidoStatus status, BigDecimal valor, LocalDateTime dataCriacao, Pagamento pagamento,List<ItemPedido> produtos) {
        this.status = status;
        this.valor = valor;
        this.dataCriacao = dataCriacao;
        this.pagamento = pagamento;
        this.produtos=produtos;
    }

    public Pedido(Cliente cliente, List<ItemPedido> produtos) {
        this.cliente = cliente;
        this.produtos = new ArrayList<>(produtos);
        this.status = PedidoStatus.PENDENTE;
    }
    public Pedido() {
    }

    public Pedido(Long id){
        this.id =id;
    }


    Carrinho carrinho;
    public Carrinho getCarrinho() {
        return carrinho;
    }
    public void setCarrinho(Carrinho carrinho) {
        this.carrinho = carrinho;
    }

    public void cancelar(){
        if (this.status == PedidoStatus.PRONTO || this.status == PedidoStatus.EM_PREPARACAO) {
            System.out.println("Pedido " + this.id + " não pode ser cancelado.");
        }
        else {
             this.status = PedidoStatus.CANCELADO;
}
    }

    public LocalTime getHoraRetirada() {
            return horaRetirada;
    }
    
    public void setHoraRetirada(LocalTime horaRetirada) {
            this.horaRetirada = horaRetirada;
    }
    public String getStatus() {
        return this.status.toString();
    }

    public Integer getQuantidadeProdutos() {
        return this.quantidadeProdutos;
    }

    public BigDecimal getValor() {
        return this.valor;
    }

    public Cliente getCliente() {
        return cliente;
    }

    public Long getId() {
        return id;
    }

    public List<ItemPedido> getProdutos() {
        return produtos;
    }

    public void setId(Long id) {
        this.id = id;
    }
    public LocalDate getDataRetirada() {
            return dataRetirada;
    }
    
    public void setDataRetirada(LocalDate dataRetirada) {
            this.dataRetirada = dataRetirada;
    }
    

    public void setValor(BigDecimal valor) {
        this.valor = valor;
    }

    public void setStatus(String status){ 
        this.status = PedidoStatus.valueOf(status);
    }

    public LocalDateTime getDataCriacao() {
        return dataCriacao;
    }

    public void setDataCriacao(LocalDateTime dataCriacao) {
        this.dataCriacao = dataCriacao;
    }

    public LocalDateTime getDataAtualizacao() {
        return dataAtualizacao;
    }

    public void setDataAtualizacao(LocalDateTime dataAtualizacao) {
        this.dataAtualizacao = dataAtualizacao;
    }

    public void setProdutos(List<ItemPedido> produtos) {
        this.produtos = produtos;
    }

    public void setCliente(Cliente cliente) {
        this.cliente = cliente;
    }

    public Pagamento getPagamento() {
        return pagamento;
    }

    public void setPagamento(Pagamento pagamento) {
        this.pagamento = pagamento;
    }

    public Endereco getEndereco() {
        return endereco;
    }

    public void setEndereco(Endereco endereco) {
        this.endereco = endereco;
    }

    public Loja getLoja() {
        return loja;
    }

    public void setLoja(Loja loja) {
        this.loja = loja;
    }
}
