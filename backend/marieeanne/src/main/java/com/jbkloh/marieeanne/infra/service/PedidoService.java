package com.jbkloh.marieeanne.infra.service;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.LocalDate;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.jbkloh.marieeanne.core.models.Carrinho;
import com.jbkloh.marieeanne.core.models.Cliente;
import com.jbkloh.marieeanne.core.models.Pedido;
import com.jbkloh.marieeanne.core.usecases.PedidoUseCase;
import com.jbkloh.marieeanne.infra.dtos.cliente.ClienteRequestDTO;
import com.jbkloh.marieeanne.infra.dtos.itemPedido.ItemPedidoResponseDTO;
import com.jbkloh.marieeanne.infra.dtos.pagamento.PagamentoResponseDTO;
import com.jbkloh.marieeanne.infra.dtos.pagamento.PaymentRequestDTO;
import com.jbkloh.marieeanne.infra.dtos.pedido.PedidoResponseDTO;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service 
@Transactional
@Slf4j
@RequiredArgsConstructor
public class PedidoService  {
        private final PedidoUseCase pedidoUseCase;
        private final ClienteService clienteService;
        private final CarrinhoService carrinhoService;

        @Transactional
        public Pedido criarPedidoCarrinho(Carrinho carrinho, Cliente cliente, LocalDate dataRetirada, LocalTime horarioRetirada) {
            return pedidoUseCase.criarPedido(carrinho, cliente, dataRetirada, horarioRetirada);
        }
        @Transactional(readOnly=true)
        public Pedido buscarPedidoPendentePorClienteId(Long clienteId) {
                return pedidoUseCase.findPedidoPendenteByClienteId(clienteId);
        }
        @Transactional(readOnly=true)
        public Pedido buscarPorId(Long id) {
                return pedidoUseCase.buscarPorId(id);
        }
        @Transactional(readOnly=true)
        public Pedido buscarPorClienteId(Long id) {
                return pedidoUseCase.buscarPorCliente(id);
        }
        @Transactional(readOnly=true)        
        public List<PedidoResponseDTO> findAll() {
                return pedidoUseCase.buscarTodos()
                                .stream()
                                .map(this::convertToDTO)
                                .collect(Collectors.toList());
        }

        @Transactional(readOnly=true)        
        public PedidoResponseDTO findOrderById(Long id) {
                return convertToDTO(pedidoUseCase.buscarPorId(id));
        }
        
        @Transactional(readOnly=true)        
        public List<PedidoResponseDTO> findOrdersByUserId(Long userId) {
                return pedidoUseCase.buscarTodosPorUserId(userId)
                                .stream()
                                .map(this::convertToDTO)
                                .collect(Collectors.toList());
        }

        @Transactional(readOnly = true)
    public List<PedidoResponseDTO> filtrarPedidosPagosPorData(LocalDateTime dataCriacao) {
        List<Pedido> pedidos = pedidoUseCase.filtrarPedidosPagosPorData(dataCriacao);
        return pedidos.stream()
        .map(this::convertToDTO)
        .toList();
    }
    @Transactional(readOnly = true)
    public List<PedidoResponseDTO> filtrarPedidosPagosPorPeriodo(LocalDateTime dataInicio, LocalDateTime dataFim) {
        List<Pedido> pedidos = pedidoUseCase.filtrarPedidosPagosPorPeriodo(dataInicio, dataFim);
        return pedidos.stream()
        .map(this::convertToDTO)
        .toList();
    }
    @Transactional(readOnly = true)
    public List<PedidoResponseDTO> filtrarPedidosPorStatusEData(String status, LocalDateTime dataCriacao) {
        List<Pedido> pedidos = pedidoUseCase.filtrarPedidosPorStatusEData(status.toUpperCase(), dataCriacao);
        return pedidos.stream()
        .map(this::convertToDTO)
        .toList();
    }
    @Transactional(readOnly = true)
    public List<PedidoResponseDTO> filtrarPedidosPorStatusEPeriodo(String status, LocalDateTime dataInicio, LocalDateTime dataFim) {
        List<Pedido> pedidos = pedidoUseCase.filtrarPedidosPorStatusEPeriodo(status.toUpperCase(), dataInicio,dataFim);
        return pedidos.stream()
        .map(this::convertToDTO)
        .toList();
    }

    @Transactional
    public void processarPedidoWebhook(
        Long idPedido,
        String action,
        String type,
        String transacaoId,
        LocalDateTime dataCreated){
            Pedido pedido = pedidoUseCase.buscarPorId(idPedido);
            log.info("O pedido foi encontrado e está sendo enviado ao use case com id: "+pedido.getId());
            pedidoUseCase.processarPedidoWebhook(
                pedido,
                action,
                type,
                transacaoId,
                dataCreated);

    }

    public void cancelarPedido(Long idPedido){
         pedidoUseCase.cancelarPedido(idPedido);
    
    }

    private PedidoResponseDTO convertToDTO(Pedido p) {
        PagamentoResponseDTO pagamentoDTO = new PagamentoResponseDTO(
        p.getPagamento().getId(),
        p.getPagamento().getValor(),
        p.getPagamento().getParcelas(),
        p.getPagamento().getStatusPagamento().toString(),
        p.getPagamento().getDataPagamento()
        );    

        List<ItemPedidoResponseDTO> itensDTO = p.getProdutos().stream()
            .map(item -> new ItemPedidoResponseDTO(
            null, 
            item.getProduto().getId(),
            item.getProduto().getProduto().getNome(),
            item.getQuantidade(),
            item.getPrecoUnitario()
        ))
        .toList();

    return new PedidoResponseDTO(
        pagamentoDTO,
        p.getQuantidadeProdutos(),
        itensDTO,
        p.getDataCriacao(),
        p.getValor(),
        p.getStatus()
    );
    }
}
