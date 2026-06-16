package com.jbkloh.marieeanne.infra.controllers;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.jbkloh.marieeanne.infra.dtos.produto.ProdutoCardapioResponse;
import com.jbkloh.marieeanne.infra.service.ProdutoService;

@RestController
@RequestMapping("/api/v1/produtos")
public class CardapioController {

    @Autowired
    private  ProdutoService produtoService;

    @GetMapping
    public ResponseEntity<List<ProdutoCardapioResponse>> listarParaClientes(@RequestParam("lojaId") Long lojaId) {
        List<ProdutoCardapioResponse> cardapio = produtoService.listarCardapioAtivo(lojaId);
        return ResponseEntity.ok(cardapio);
    }
}
