package com.jbkloh.marieeanne.core.usecases;

import java.util.List;

import com.jbkloh.marieeanne.core.models.Produto;

public interface ProdutoUseCase {
     Produto saveProduto(Produto produto);
     void criarProduto(Produto produto);
     void deletarProduto(Long id);
     void alternarDisponibilidade(Long id, boolean status);
     List<Produto> buscarTodos();
     Produto buscarPorNome(String nome);
     Produto buscarPorID(Long id);
     void save(Produto produto);
}
