package com.jbkloh.marieeanne.core.usecases;



public interface PagamentoUseCase {

    /**
     * Valida se o pedido pode receber um pagamento.
     */
     void validarPedidoParaPagamento(Long pedidoId);

    /**
     * Processa a mudança de estado após a resposta do Gateway.
     */
     void registrarResultadoPagamento(Long pedidoId, String statusMP, String qrCode, String qrCodeBase64);
}