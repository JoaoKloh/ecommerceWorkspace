package com.jbkloh.marieeanne.core.usecases.impl;

import java.util.Optional;

import com.jbkloh.marieeanne.core.models.Pedido;
import com.jbkloh.marieeanne.core.models.enums.PagamentoStatus;
import com.jbkloh.marieeanne.core.ports.PedidoRepositoryPort;
import com.jbkloh.marieeanne.core.usecases.PagamentoUseCase;
import com.jbkloh.marieeanne.core.usecases.PedidoUseCase;

public class PagamentoUseCaseImpl implements PagamentoUseCase {

    private final PedidoRepositoryPort pedidoRepositoryPort;
    private final PedidoUseCase pedidoUseCase;

    public PagamentoUseCaseImpl(PedidoRepositoryPort pedidoRepositoryPort, PedidoUseCase pedidoUseCase) {
        this.pedidoUseCase = pedidoUseCase;
        this.pedidoRepositoryPort = pedidoRepositoryPort;
    }

    @Override
    public void validarPedidoParaPagamento(Long pedidoId) {
        Optional<Pedido> pedidoOptional = pedidoRepositoryPort.findByIdComProdutos(pedidoId);
        Pedido pedido = pedidoOptional.orElseThrow(() -> new RuntimeException("Pedido não encontrado."));


        // Use Enums ou constantes do domínio, evite comparar Strings como "PAGO"
        if ("PAGO".equals(pedido.getStatus()) || "CANCELADO".equals(pedido.getStatus())) {
            throw new IllegalStateException("Este pedido não permite mais alterações de pagamento (Finalizado/Cancelado).");
        }
    }

    @Override
    public void registrarResultadoPagamento(Long pedidoId, String statusMP, String qrCode, String qrCodeBase64) {
        Pedido pedido = pedidoRepositoryPort.findById(pedidoId);
        if (pedido == null) throw new RuntimeException("Pedido não encontrado para atualização.");

        // 2. Orquestração da Mudança de Estado
        processarStatusGateway(pedido, statusMP, qrCode != null);

        pedidoRepositoryPort.update(pedido);
    }
    
    private void processarStatusGateway(Pedido pedido, String status, boolean isPix) {
        switch (status.toLowerCase()) {
            case "approved" -> confirmarPagamento(pedido);
            case "rejected" -> {
                pedido.setStatus("PAGAMENTO_REJEITADO");
                pedido.getPagamento().setStatusPagamento(PagamentoStatus.REJEITADO);
            }
            case "pending", "in_process" -> {
                String novoStatus = isPix ? "AGUARDANDO_PAGAMENTO_PIX" : "AGUARDANDO_PAGAMENTO_CARTAO";
                pedido.setStatus(novoStatus);
                pedido.getPagamento().setStatusPagamento(PagamentoStatus.PENDENTE);
            }
            case "cancelled" -> {
                pedido.setStatus("CANCELADO");
                pedido.getPagamento().setStatusPagamento(PagamentoStatus.CANCELADO);
            }
            default -> {
                pedido.setStatus("FALHA_NO_PAGAMENTO");
                pedido.getPagamento().setStatusPagamento(PagamentoStatus.FALHA);
            }
        }
    }

    private void confirmarPagamento(Pedido pedido) {
        // Regra de Ouro: Só baixa estoque se o status anterior não for PAGO (Evita duplicidade)
        if (!"PAGO".equals(pedido.getStatus())) {
            pedido.setStatus("PAGO");
            pedido.getPagamento().setStatusPagamento(PagamentoStatus.APROVADO);
            
            // Integridade: Delegar para PedidoUseCase a responsabilidade de reduzir estoque
            pedidoUseCase.baixaNoEstoque(pedido.getId());
        }
    }
}