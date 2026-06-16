package com.jbkloh.marieeanne.core.usecases;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.LocalDateTime;
import java.util.List;

import com.jbkloh.marieeanne.core.models.Carrinho;
import com.jbkloh.marieeanne.core.models.Cliente;
import com.jbkloh.marieeanne.core.models.Pedido;



public interface PedidoUseCase {
    Pedido buscarPorId(Long id);
    Pedido criarPedido(Carrinho carrinho, Cliente cliente, LocalDate dataRetirada, LocalTime horarioRetirada); 
    Pedido findPedidoPendenteByClienteId(Long clienteId);
    Pedido buscarPorCliente(Long idCliente);
    void cancelarPedidosExpirados(LocalDateTime linhaDeCorte);      
    void cancelarPedido(Long id);
    void atualizarStatusPedido(Long id, String status);
    void baixaNoEstoque(Long pedidoId);
    void processarPedidoWebhook(Pedido p,String action,String type,String transacaoId,LocalDateTime dataCreated);
    List<Pedido> filtrarPedidosPagosPorData(LocalDateTime dataCriacao);
    List<Pedido> filtrarPedidosPagosPorPeriodo(LocalDateTime dataInicio, LocalDateTime dataFim);
    List<Pedido> filtrarPedidosPorStatusEData(String status, LocalDateTime dataCriacao);
    List<Pedido> filtrarPedidosPorStatusEPeriodo(String status, LocalDateTime dataInicio, LocalDateTime dataFim);
    List<Pedido> buscarTodos();
    List<Pedido> buscarTodosPorUserId(Long idUser);
}
