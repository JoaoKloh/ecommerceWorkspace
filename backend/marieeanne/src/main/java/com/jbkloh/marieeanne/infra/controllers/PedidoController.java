package com.jbkloh.marieeanne.infra.controllers;



import java.util.List;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.jbkloh.marieeanne.core.models.Carrinho;
import com.jbkloh.marieeanne.core.models.Pedido;
import com.jbkloh.marieeanne.core.models.enums.PagamentoStatus;
import com.jbkloh.marieeanne.infra.dtos.pagamento.ProcessamentoPgRequestDTO;
import com.jbkloh.marieeanne.infra.dtos.pedido.PedidoCreateResponseDTO;
import com.jbkloh.marieeanne.infra.dtos.pedido.PedidoCreateRequestDTO;
import com.jbkloh.marieeanne.infra.dtos.pedido.PedidoResponseDTO;
import com.jbkloh.marieeanne.infra.service.CarrinhoService;
import com.jbkloh.marieeanne.infra.service.ClienteService;
import com.jbkloh.marieeanne.infra.service.PagamentoService;
import com.jbkloh.marieeanne.infra.service.PedidoService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/pedido")
@RequiredArgsConstructor
public class PedidoController {
    private final PedidoService pedidoService;
    private final CarrinhoService carrinhoService;
    private final ClienteService clienteService;
    private final PagamentoService pagamentoService;

    @PostMapping("/createOrder")
    public ResponseEntity<PedidoCreateResponseDTO> criar(@RequestBody @Valid PedidoCreateRequestDTO req) {
            Carrinho carrinho = carrinhoService.obterCarrinhoDoCliente();
            Pedido pedido = pedidoService.criarPedidoCarrinho(
                    carrinho,
                    clienteService.buscarPorEmail(
                    carrinho.getCliente().getEmail()), 
                    req.dataRetirada(),
                    req.horaRetirada());
            String preferenceid=pagamentoService.criarPreferenciaPagamento(pedido);
            return ResponseEntity.ok().body(new PedidoCreateResponseDTO(pedido.getId(), preferenceid, pedido.getValor()));
    }
    @GetMapping("/meusPedidos")
    public ResponseEntity<List<PedidoResponseDTO>> meusPedidos() {
        Long userId = clienteService.getAuthenticatedUserId();
        List<PedidoResponseDTO> pedidos = pedidoService.findOrdersByUserId(userId);
        return ResponseEntity.ok().body(pedidos);
    }
    @PostMapping("/solicitarPagamento")
    public ResponseEntity<?> solicitarPagamentoAoMp(@RequestBody @Valid ProcessamentoPgRequestDTO req) {
        Pedido pedido = pedidoService.buscarPorId(req.idPedido());

        if (!pedido.getPagamento().getStatusPagamento().equals(PagamentoStatus.PENDENTE)) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                             .body("Este pedido já foi pago ou cancelado.");
        }
        Map<String, Object> resultadoMp = pagamentoService.enviarProcessamentoAoMp(req, pedido);        
        return ResponseEntity.ok().body(resultadoMp);
            
    }
}
    
