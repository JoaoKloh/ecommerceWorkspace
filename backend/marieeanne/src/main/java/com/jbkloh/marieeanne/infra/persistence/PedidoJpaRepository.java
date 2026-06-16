package com.jbkloh.marieeanne.infra.persistence;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.jbkloh.marieeanne.infra.models.PedidoEntity;

@Repository
public interface PedidoJpaRepository extends JpaRepository<PedidoEntity, Long> {

    List<PedidoEntity> findAllByClienteId(Long id);

    Optional<PedidoEntity> findByClienteId(Long id);

    @Query("SELECT p FROM PedidoEntity p LEFT JOIN FETCH p.produtos WHERE p.id = :id")
    Optional<PedidoEntity> findByIdComProdutos(@Param("id") Long id);

    @Query("SELECT p FROM PedidoEntity p WHERE p.status = 'PENDENTE' AND p.dataCriacao < :linhaDeCorte")
    List<PedidoEntity> findPedidosPendenteAntesDe(@Param("linhaDeCorte") LocalDateTime linhaDeCorte);
    
    @Query("SELECT p FROM PedidoEntity p WHERE p.cliente.id = :clienteId AND p.status = 'PENDENTE'")
    Optional<PedidoEntity>findPedidoPendenteByClienteId(Long clienteId);

    @Query("SELECT p FROM PedidoEntity p WHERE p.status = 'PAGO' AND p.dataCriacao = :dataCriacao ")
    List<PedidoEntity> filtrarPedidosPagosPorData(@Param("dataCriacao") LocalDateTime dataCriacao);

    @Query("SELECT p FROM PedidoEntity p WHERE p.status = 'PAGO' AND p.dataCriacao >= :dataInicio AND p.dataCriacao <= :dataFim")
    List<PedidoEntity> filtrarPedidosPagosPorPeriodo(@Param("dataInicio") LocalDateTime dataInicio, @Param("dataFim") LocalDateTime dataFim);

    @Query("SELECT p FROM PedidoEntity p WHERE p.status = :status AND p.dataCriacao = :dataCriacao")
    List<PedidoEntity> filtrarPedidosPorStatusEData(@Param("status") String status, @Param("dataCriacao") LocalDateTime dataCriacao);

    @Query("SELECT p FROM PedidoEntity p WHERE p.status = :status AND p.dataCriacao >= :dataInicio AND p.dataCriacao <= :dataFim")
    List<PedidoEntity> filtrarPedidosPorStatusEPeriodo(@Param("status") String status, @Param("dataInicio") LocalDateTime dataInicio, @Param("dataFim") LocalDateTime dataFim);

}
