package com.jbkloh.marieeanne.infra.controllers;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.jbkloh.marieeanne.core.models.Loja;
import com.jbkloh.marieeanne.infra.dtos.loja.LojaCreateRequestDTO;
import com.jbkloh.marieeanne.infra.service.LojaService;

import jakarta.validation.Valid;


@RestController
@RequestMapping("/api/v1/pontos")
public class LojaController {
    @Autowired
    private LojaService lojaService;

    @PostMapping("/inserirPonto")
    public ResponseEntity<?> criarPonto(@RequestBody @Valid LojaCreateRequestDTO dto){
        lojaService.criarLoja(dto);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/deletarPonto/{idLoja}")
    public ResponseEntity<?>deletarPonto(@PathVariable Long idLoja){
        lojaService.deletarLoja(idLoja);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/buscarPontos")
    public ResponseEntity<List<Loja>>recuperarPontos(){
        List<Loja> lojas = lojaService.buscarTodos();
        return ResponseEntity.ok().body(lojas);
    }
}
