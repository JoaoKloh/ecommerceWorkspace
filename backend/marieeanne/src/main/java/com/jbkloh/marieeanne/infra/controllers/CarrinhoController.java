package com.jbkloh.marieeanne.infra.controllers;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.jbkloh.marieeanne.core.models.Carrinho;
import com.jbkloh.marieeanne.infra.dtos.carrinho.CarrinhoRequestDTO;
import com.jbkloh.marieeanne.infra.dtos.carrinho.CarrinhoResponseDTO;
import com.jbkloh.marieeanne.infra.dtos.itemCarrinho.itemCarrinhoResponseDTO;
import com.jbkloh.marieeanne.infra.service.CarrinhoService;

import jakarta.validation.Valid;


@RequestMapping("/api/v1/carrinho")
@RestController
public class CarrinhoController {

    @Autowired
    private CarrinhoService carrinhoService;

    @GetMapping
    public ResponseEntity<CarrinhoResponseDTO> obterCarrinho(){
        Carrinho carrinho = carrinhoService.obterCarrinhoDoCliente();
        List<itemCarrinhoResponseDTO> itensDTO = carrinho.getProdutos().stream()
        .map(item -> new itemCarrinhoResponseDTO(
            item.getProduto().getId(),
            item.getProduto().getProduto().getNome(),
            item.getQuantidade(),
            item.getPrecoUnitario(), 
            carrinho.getValorTotal())        
        )
        .toList();
        return ResponseEntity.ok().body(new CarrinhoResponseDTO(itensDTO, carrinhoService.verTotal()));
        
    }
    @DeleteMapping("/limparCarrinho")
    public ResponseEntity<Void> limparCarrinho(){
        carrinhoService.esvaziarCarrinho();
        return ResponseEntity.noContent().build();
    
    }
    @PostMapping("/adicionarItem")
    public ResponseEntity<CarrinhoResponseDTO> adicionarItemAoCarrinho(@Valid @RequestBody CarrinhoRequestDTO carrinhoRequestDTO){
        return ResponseEntity.ok().body(carrinhoService.adicionarItem(carrinhoRequestDTO.idItem(), carrinhoRequestDTO.quantidade()));
    }
    @DeleteMapping("/removerItem/{id}")
    public ResponseEntity<Void>removerItemDoCarrinho(@PathVariable Long id){
        carrinhoService.removerItem(id);
        return ResponseEntity.noContent().build();
    }
    @PutMapping("/decrementarQuantidade")
    public ResponseEntity<CarrinhoResponseDTO>removerQuantidadeItem(@Valid @RequestBody CarrinhoRequestDTO carrinhoRequestDTO){
        return ResponseEntity.ok().body(carrinhoService.removerUnidadeItem(carrinhoRequestDTO.idItem(), carrinhoRequestDTO.quantidade()));
    }
}
