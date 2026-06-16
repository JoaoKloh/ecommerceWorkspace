package com.jbkloh.marieeanne.core.ports;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import com.jbkloh.marieeanne.core.models.Pedido;

public interface PedidoRepositoryPort {
    Pedido save(Pedido pedido);
    Optional<Pedido> findByIdComProdutos(Long id);
    Optional<Pedido> findyByClienteId(Long id);
    Pedido findById(Long id);
    void delete(Long id);
    void update(Pedido pedido);
    List<Pedido> findPedidosPendenteAntesDe(LocalDateTime linhaDeCorte);
    Optional<Pedido> findPedidoPendenteByClienteId(Long clienteId);
    List<Pedido> findAll();
    List<Pedido> findAllByUserId(Long userId);
    List<Pedido> filtrarPedidosPagosPorData(LocalDateTime dataCriacao);
    List <Pedido> filtrarPedidosPagosPorPeriodo(LocalDateTime dataInicio, LocalDateTime dataFim);
    List<Pedido> filtrarPedidosPorStatusEData(String status, LocalDateTime dataCriacao);
    List<Pedido> filtrarPedidosPorStatusEPeriodo(String status, LocalDateTime dataInicio, LocalDateTime dataFim);
    
}
