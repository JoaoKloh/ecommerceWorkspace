package com.jbkloh.marieeanne.infra.adpters;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import com.jbkloh.marieeanne.core.models.Pedido;
import com.jbkloh.marieeanne.core.ports.PedidoRepositoryPort;
import com.jbkloh.marieeanne.infra.exceptions.AppException;
import com.jbkloh.marieeanne.infra.mappers.PedidoMapper;
import com.jbkloh.marieeanne.infra.models.PedidoEntity;
import com.jbkloh.marieeanne.infra.persistence.PedidoJpaRepository;

import lombok.AllArgsConstructor;

@Component
@AllArgsConstructor
public class PedidoAdpter implements PedidoRepositoryPort {

    private final PedidoJpaRepository pedidoJpaRepository;

    @Override
    public Pedido save(Pedido pedido) {
        PedidoEntity pedidoEntity = PedidoMapper.PedidoDomainToPedidoEntity(pedido);
        return PedidoMapper.EntityToDomain(pedidoJpaRepository.save(pedidoEntity));
    }


    @Override
    public Pedido findById(Long id) {
        return this.pedidoJpaRepository.findById(id)
                .map(PedidoMapper::EntityToDomain)
                .orElseThrow(() -> new AppException("Pedido não encontrado",HttpStatus.NOT_FOUND));
    }
    
    @Override
    public Optional<Pedido> findByIdComProdutos(Long id) {
        return this.pedidoJpaRepository.findByIdComProdutos(id)
                .map(PedidoMapper::EntityToDomain);
    }

    @Override
    public void delete(Long id) {
        PedidoEntity pedidoEntity = this.pedidoJpaRepository.findById(id)
                .orElseThrow(() -> new AppException("Pedido não encontrado",HttpStatus.NOT_FOUND));
        this.pedidoJpaRepository.delete(pedidoEntity);
    }

    @Override
    public void update(Pedido pedido) {
        PedidoEntity existing = this.pedidoJpaRepository.findById(pedido.getId())
            .orElseThrow(() -> new AppException("Pedido não encontrado", HttpStatus.NOT_FOUND));
    
        existing.setStatus(pedido.getStatus());
    
        this.pedidoJpaRepository.save(existing);
    }


    @Override
    public List<Pedido> filtrarPedidosPagosPorData(LocalDateTime dataCriacao) {
        List<PedidoEntity> pedidosEntity = this.pedidoJpaRepository.filtrarPedidosPagosPorData(dataCriacao);
        return pedidosEntity.stream()
                .map(PedidoMapper::EntityToDomain)
                .toList();
    }

    @Override
    public List<Pedido> filtrarPedidosPagosPorPeriodo(LocalDateTime dataInicio, LocalDateTime dataFim) {
        List<PedidoEntity> pedidosEntity = this.pedidoJpaRepository.filtrarPedidosPagosPorPeriodo(dataInicio, dataFim);
        return pedidosEntity.stream()
                .map(PedidoMapper::EntityToDomain)
                .toList();
    }

    @Override
    public List<Pedido> filtrarPedidosPorStatusEData(String status, LocalDateTime dataCriacao) {
        List<PedidoEntity> pedidosEntity = this.pedidoJpaRepository.filtrarPedidosPorStatusEData(status, dataCriacao);
        return pedidosEntity.stream()
                .map(PedidoMapper::EntityToDomain)
                .toList();
    }

    @Override
    public List<Pedido> filtrarPedidosPorStatusEPeriodo(String status, LocalDateTime dataInicio,LocalDateTime dataFim) {
        List<PedidoEntity> pedidosEntity = this.pedidoJpaRepository.filtrarPedidosPorStatusEPeriodo(status, dataInicio, dataFim);
        return pedidosEntity.stream()
                .map(PedidoMapper::EntityToDomain)
                .toList();
    }

    @Override
    public List<Pedido> findAll() {
        return pedidoJpaRepository.findAll().stream()
        .map(PedidoMapper::EntityToDomain)
        .toList();
    }

    @Override
    public List<Pedido> findAllByUserId(Long userId) {
        return pedidoJpaRepository.findAllByClienteId(userId).stream()
        .map(PedidoMapper::EntityToDomain)
        .toList();
    }

    @Override
    public Optional<Pedido> findPedidoPendenteByClienteId(Long clienteId) {
        return pedidoJpaRepository.findPedidoPendenteByClienteId(clienteId)
                .map(PedidoMapper::EntityToDomain);
    }

    @Override
    public List<Pedido> findPedidosPendenteAntesDe(LocalDateTime linhaDeCorte) {
        return pedidoJpaRepository.findPedidosPendenteAntesDe(linhaDeCorte).stream()
                .map(PedidoMapper::EntityToDomain)
                .toList();
    }

    @Override
    public Optional<Pedido> findyByClienteId(Long id) {
        return pedidoJpaRepository.findByClienteId(id).map(PedidoMapper::EntityToDomain);
    }
}
