package com.jbkloh.marieeanne.core.usecases;

import java.util.List;

import com.jbkloh.marieeanne.core.models.ItemPedido;
import com.jbkloh.marieeanne.core.models.ProdutoLoja;

public interface ProdutoLojaUseCase {

    ProdutoLoja findById(Long idProduto);
    ProdutoLoja save(ProdutoLoja produtoLoja);
    void atualizarEstoquePedido(List<ItemPedido> itens);
    void atualizarEstoqueProduto(Long id, Integer qtd);
    List<ProdutoLoja> listarProdutosDisponiveisComEstoque(Long idLoja);
    List<ProdutoLoja> findAll();
}
