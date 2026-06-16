package com.jbkloh.marieeanne.infra.controllers;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jbkloh.marieeanne.core.models.Cliente;
import com.jbkloh.marieeanne.core.models.ProdutoLoja;
import com.jbkloh.marieeanne.infra.dtos.cliente.ClienteResponseDTO;
import com.jbkloh.marieeanne.infra.dtos.endereco.EnderecoResponseDto;
import com.jbkloh.marieeanne.infra.dtos.pedido.PedidoResponseDTO;
import com.jbkloh.marieeanne.infra.dtos.produto.ProdutoLojaAdminResponse;
import com.jbkloh.marieeanne.infra.dtos.produto.ProdutoRequestDTO;
import com.jbkloh.marieeanne.infra.service.ClienteService;
import com.jbkloh.marieeanne.infra.service.PedidoService;
import com.jbkloh.marieeanne.infra.service.ProdutoLojaService;
import com.jbkloh.marieeanne.infra.service.ProdutoService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/v1/admin")
public class AdminController {

    @Autowired
    private ProdutoService produtoService;
    @Autowired
    private ProdutoLojaService produtoLojaService;
    @Autowired
    private PedidoService pedidoService;
    @Autowired
    private ClienteService clienteService;

    @PostMapping(path="/cadastrarproduto", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ProdutoLoja> cadastrar(
            @RequestParam("lojaId") Long idLoja, 
            @RequestPart("produto") @Valid ProdutoRequestDTO produtoDto,
            @RequestPart("imagem") MultipartFile arquivo) throws IOException {

        ProdutoLoja salvo = produtoLojaService.criarProduto(idLoja, produtoDto, arquivo);
                
        return ResponseEntity.status(HttpStatus.CREATED).body(salvo);
    }

    @PatchMapping("/autualizar/estoque/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Transactional
    public ResponseEntity<Void> atualizarEstoque(@PathVariable Long id, @RequestParam Integer quantidade) {
        produtoLojaService.atualizarEstoque(id, quantidade);
        return ResponseEntity.noContent().build();
    }

    @PutMapping(value="/atualizar/produto/{id}",consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("hasRole('ADMIN')")
    @Transactional
    public ResponseEntity<Void> atualizarProduto(
        @RequestPart("produto") String produtoJson, 
        @RequestPart(value = "imagem", required = false) MultipartFile imagem,
        @PathVariable Long id) throws JsonProcessingException, IOException{
            ObjectMapper objectMapper = new ObjectMapper();
            ProdutoRequestDTO produtoRequestDTO = objectMapper.readValue(produtoJson, ProdutoRequestDTO.class);

            byte[] imgBytes = (imagem != null && !imagem.isEmpty()) ? imagem.getBytes() : null;
            String imgNome = (imagem != null && !imagem.isEmpty()) ? imagem.getOriginalFilename() : null;
            
            produtoService.atualizarProduto(produtoRequestDTO,imgBytes,imgNome,id);
            return ResponseEntity.noContent().build();
    }

    @PatchMapping("/alterardisponibilidade/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Transactional
    public ResponseEntity<Void> alternarDisponibilidade(@PathVariable Long id, @RequestParam boolean status) {
        produtoService.alternarDisponibilidade(id, status);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/lista-completa")
    @PreAuthorize("hasRole('ADMIN')")
    @Transactional(readOnly = true)
    public ResponseEntity<List<ProdutoLojaAdminResponse>> listarTudoAdmin() {
        return ResponseEntity.ok().body(produtoLojaService.findAllAdmin());
    }

    @DeleteMapping("/deletar/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Transactional()
    public ResponseEntity<Void> deletar(@PathVariable Long id) {
        produtoService.deletar(id);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/cancelar/{idPedido}")
    @Transactional()
    public ResponseEntity<Void>cancelarPedido(@PathVariable Long idPedido){
        pedidoService.cancelarPedido(idPedido);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/filtraPedidosData")
    @PreAuthorize("hasRole('ADMIN')")
    @Transactional(readOnly = true)
    public ResponseEntity<List<PedidoResponseDTO>> filtrarPedidosPagosPorData(
        @RequestParam("data") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime dataCriacao) {
        return ResponseEntity.ok().body(pedidoService.filtrarPedidosPagosPorData(dataCriacao));
    }
    
    @GetMapping("/filtraPedidosPeriodo")
    @PreAuthorize("hasRole('ADMIN')")
    @Transactional(readOnly = true)
    public ResponseEntity<List<PedidoResponseDTO>> filtrarPedidosPagosPorPeriodo(
        @RequestParam("inicio") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDateTime inicio,
        @RequestParam("fim") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDateTime fim) {
            return ResponseEntity.ok().body(pedidoService.filtrarPedidosPagosPorPeriodo(inicio,fim));
    }

    @GetMapping("/filtraPedidosStatusData")
    @PreAuthorize("hasRole('ADMIN')")
    @Transactional(readOnly = true)
    public ResponseEntity<List<PedidoResponseDTO>> filtrarPedidosPorStatusEData(
        @RequestParam("status") String status,
        @RequestParam @DateTimeFormat(iso=DateTimeFormat.ISO.DATE) LocalDateTime dataCriacao){
            return ResponseEntity.ok().body(pedidoService.filtrarPedidosPorStatusEData(status, dataCriacao));
        }
    
    
    @GetMapping("/filtraPedidosStatusPeriodo")
    @PreAuthorize("hasRole('ADMIN')")
    @Transactional(readOnly = true)
    public ResponseEntity<List<PedidoResponseDTO>> filtrarPedidosPorStatusEPeriodo(
        @RequestParam("status") String status,
        @RequestParam("inicio") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDateTime inicio,
        @RequestParam("fim") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDateTime fim) {
            return ResponseEntity.ok().body(pedidoService.filtrarPedidosPorStatusEPeriodo(status, inicio, fim));
    }

    @GetMapping("/filtrarClientesPorEmail")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ClienteResponseDTO> filtrarClientesPorEmail(
        @RequestBody String email
    ){
        Cliente cliente= clienteService.buscarPorEmail(email);
        EnderecoResponseDto endereco = new EnderecoResponseDto(
            cliente.getEndereco().getId(), 
            cliente.getEndereco().getRua(), 
            cliente.getEndereco().getNumero(), 
            cliente.getEndereco().getComplemento(), 
            cliente.getEndereco().getBairro(), 
            cliente.getEndereco().getCidade(), 
            cliente.getEndereco().getEstado(), 
            cliente.getEndereco().getCep());
        return ResponseEntity.ok().body(new ClienteResponseDTO(
            cliente.getId(), 
            cliente.getNome(), 
            cliente.getTelefone(), 
            endereco, 
            cliente.getDataNascimento()));
    }
}
