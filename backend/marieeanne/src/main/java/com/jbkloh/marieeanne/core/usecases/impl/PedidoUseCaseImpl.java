package com.jbkloh.marieeanne.core.usecases.impl;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

import com.jbkloh.marieeanne.core.models.Carrinho;
import com.jbkloh.marieeanne.core.models.Cliente;
import com.jbkloh.marieeanne.core.models.ItemPedido;
import com.jbkloh.marieeanne.core.models.Loja;
import com.jbkloh.marieeanne.core.models.Pagamento;
import com.jbkloh.marieeanne.core.models.Pedido;
import com.jbkloh.marieeanne.core.models.ProdutoLoja;
import com.jbkloh.marieeanne.core.models.enums.PagamentoStatus;
import com.jbkloh.marieeanne.core.models.enums.PedidoStatus;
import com.jbkloh.marieeanne.core.ports.PedidoRepositoryPort;
import com.jbkloh.marieeanne.core.ports.ProdutoLojaRepositoryPort;
import com.jbkloh.marieeanne.core.usecases.PedidoUseCase;
import com.jbkloh.marieeanne.core.usecases.ProdutoLojaUseCase;

public class PedidoUseCaseImpl implements PedidoUseCase {
    private final PedidoRepositoryPort pedidoRepositoryPort;
    private final ProdutoLojaRepositoryPort produtoLojaRepositoryPort;
    private final ProdutoLojaUseCase produtoLojaUseCase;

    public PedidoUseCaseImpl(PedidoRepositoryPort pedidoRepositoryPort, ProdutoLojaRepositoryPort produtoLojaRepositoryPort, ProdutoLojaUseCase produtoLojaUseCase1) {
        this.pedidoRepositoryPort = pedidoRepositoryPort;
        this.produtoLojaRepositoryPort = produtoLojaRepositoryPort;
        this.produtoLojaUseCase = produtoLojaUseCase1;
    }   

    @Override
    public Pedido buscarPorId(Long id) {
        Pedido pedido = pedidoRepositoryPort.findById(id);
        validarPedidoExistente(pedido);
        return pedido;
    }

    @Override
    public Pedido criarPedido(Carrinho carrinho, Cliente cliente, LocalDate dataRetirada, LocalTime horarioRetirada) {
        if (carrinho.getProdutos() == null || carrinho.getProdutos().isEmpty()) {
            throw new IllegalStateException("Não é possível criar um pedido com o carrinho vazio.");
        }
        BigDecimal totalProdutos = carrinho.getValorTotal();
        Pagamento pagamento = new Pagamento(totalProdutos, PagamentoStatus.PENDENTE);

        List<ItemPedido> itens = carrinho.getProdutos().stream()
            .map(item -> new ItemPedido(item.getProduto(), item.getQuantidade(), item.getPrecoUnitario()))
            .toList();
        Loja loja = itens.stream()
            .map(p->p.getProduto().getLoja())
            .findFirst()
            .orElseThrow(()-> new IllegalArgumentException("Nenhuma loja associada aos itens do carrinho"));
        Pedido pedido = new Pedido(PedidoStatus.PENDENTE, totalProdutos, LocalDateTime.now(), pagamento, itens);
        pedido.setValor(totalProdutos);
        pedido.setEndereco(cliente.getEndereco());
        pedido.setDataRetirada(dataRetirada);
        pedido.setHoraRetirada(horarioRetirada);
        itens.forEach(item -> item.setPedido(pedido));
        pedido.setCliente(cliente);
        pedido.setLoja(loja);
        
        return pedidoRepositoryPort.save(pedido);
    }

    @Override
    public void cancelarPedido(Long id) {
        Pedido pedido = this.pedidoRepositoryPort.findById(id);
        validarPedidoExistente(pedido);

        if (!PedidoStatus.PENDENTE.toString().equals(pedido.getStatus())) { 
            throw new IllegalStateException("Apenas pedidos em estado PENDENTE podem ser cancelados.");
        }

        pedido.setStatus(PedidoStatus.CANCELADO.toString());
        pedidoRepositoryPort.update(pedido);
    }

    @Override
    public void atualizarStatusPedido(Long id, String status) {
        Pedido pedido = this.pedidoRepositoryPort.findById(id);
        validarPedidoExistente(pedido);

        if(status.equals(pedido.getStatus())){
            throw new IllegalStateException("O pedido já se encontra nesse status.");
        }
        pedido.setStatus(status);
        this.pedidoRepositoryPort.update(pedido);
    }

    @Override
    public void baixaNoEstoque(Long pedidoId) {
        Pedido pedido = pedidoRepositoryPort.findById(pedidoId);
        validarPedidoExistente(pedido);

        if ("PAGO".equals(pedido.getStatus())) {
            
            Long lojaId = pedido.getLoja().getId(); 
            if (lojaId == null) {
                throw new IllegalStateException("Não é possível dar baixa no estoque: Pedido sem loja associada.");
            }

            pedido.getProdutos().forEach(item -> {
                Long produtoId = item.getProduto().getId();
                int quantidadeComprada = item.getQuantidade();

                ProdutoLoja estoqueLoja = produtoLojaRepositoryPort.findByLojaIdAndProdutoId(lojaId, produtoId)
                    .orElseThrow(() -> new IllegalStateException(
                        String.format("Produto ID %d não possui registro de estoque configurado para a Loja ID %d", 
                            produtoId, lojaId)
                    ));

                int novaQuantidadeEstoque = estoqueLoja.getQuantidade() - quantidadeComprada;

                if (novaQuantidadeEstoque < 0) {
                    throw new IllegalStateException("Estoque insuficiente na loja para o produto: " + item.getProduto().getProduto().getNome());
                }

                estoqueLoja.setQuantidade(novaQuantidadeEstoque);
                produtoLojaRepositoryPort.save(estoqueLoja);
            });
        }
    }

    @Override public List<Pedido> filtrarPedidosPagosPorData(LocalDateTime dataCriacao) { return this.pedidoRepositoryPort.filtrarPedidosPagosPorData(dataCriacao); }
    @Override public List<Pedido> filtrarPedidosPagosPorPeriodo(LocalDateTime dataInicio, LocalDateTime dataFim) { return this.pedidoRepositoryPort.filtrarPedidosPagosPorPeriodo(dataInicio, dataFim); }
    @Override public List<Pedido> filtrarPedidosPorStatusEData(String status, LocalDateTime dataCriacao) { return this.pedidoRepositoryPort.filtrarPedidosPorStatusEData(status, dataCriacao); }
    @Override public List<Pedido> filtrarPedidosPorStatusEPeriodo(String status, LocalDateTime dataInicio, LocalDateTime dataFim) { return this.pedidoRepositoryPort.filtrarPedidosPorStatusEPeriodo(status, dataInicio, dataFim); }

    @Override
    public List<Pedido> buscarTodos() {
        return pedidoRepositoryPort.findAll().stream()
            .filter(this::isPedidoValidoParaListagem)
            .toList();
    }

    @Override
    public List<Pedido> buscarTodosPorUserId(Long idUser) {
        return pedidoRepositoryPort.findAllByUserId(idUser).stream()
            .filter(this::isPedidoValidoParaListagem)
            .toList();
    }

    private void validarPedidoExistente(Pedido pedido) {
        if (pedido == null) {
            throw new RuntimeException("Pedido não encontrado.");
        }
    }

    private boolean isPedidoValidoParaListagem(Pedido pedido) {
        return pedido != null && pedido.getProdutos() != null && !pedido.getProdutos().isEmpty();
    }

    @Override
    public void cancelarPedidosExpirados(LocalDateTime linhaDeCorte) {
        List<Pedido> pedidosExpirados = pedidoRepositoryPort.findPedidosPendenteAntesDe(linhaDeCorte);
        pedidosExpirados.forEach(pedido -> {
            if(pedido.getPagamento()==null) {
                pedido.getPagamento().setStatusPagamento(PagamentoStatus.CANCELADO);
                pedido.setStatus(PedidoStatus.CANCELADO.toString());
                
                pedidoRepositoryPort.save(pedido);
                }
            }
        );
    }
 

    @Override
    public Pedido findPedidoPendenteByClienteId(Long clienteId) {
        Optional<Pedido> p = pedidoRepositoryPort.findPedidoPendenteByClienteId(clienteId);

        if(p.isEmpty()){
            throw new RuntimeException("Pedido inexistente");
        }
        return p.get();
                 
    }

    @Override
    public Pedido buscarPorCliente(Long id) {
        Optional<Pedido> p = pedidoRepositoryPort.findyByClienteId(id);

        if(p.isEmpty()){
            throw new RuntimeException("Pedido inexistente");
        }
        return p.get();
    }

    @Override
    public void processarPedidoWebhook(Pedido p, String action, String type, String transacaoId, LocalDateTime dataCreated) {
        System.out.println("====== ENTRANDO NO USECASE ======");
        System.out.println("Action recebida: " + action);
        System.out.println("Pedido ID: " + p.getId());

        switch (action) {
            case "payment.created", "created", "payment_created" -> {
                p.setDataAtualizacao(dataCreated);
                p.setStatus("PAGO");
                if (p.getPagamento() != null) {
                    p.getPagamento().setTransacaoIdMercadoPago(transacaoId);
                }
                produtoLojaUseCase.atualizarEstoquePedido(p.getProdutos());
                pedidoRepositoryPort.save(p);
                System.out.println("====== STATUS ATUALIZADO PARA PAGO ======");
            }
            case "payment.rejected", "rejected", "payment_rejected" -> {
                p.setDataAtualizacao(dataCreated);
                p.setStatus("PENDENTE");
                pedidoRepositoryPort.save(p);
            }
            default -> {
                
                if ("payment.canceled".equals(action) || "canceled".equals(action)) {
                    p.setDataAtualizacao(dataCreated);
                    p.setStatus("CANCELADO");
                    if (p.getPagamento() != null) {
                        p.getPagamento().setStatusPagamento(PagamentoStatus.FALHA);
                        p.getPagamento().setDataPagamento(dataCreated);
                    }
                    pedidoRepositoryPort.save(p);
                }
            }
        }
    }
}