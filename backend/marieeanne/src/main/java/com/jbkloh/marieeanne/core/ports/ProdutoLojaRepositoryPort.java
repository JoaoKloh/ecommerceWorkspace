package com.jbkloh.marieeanne.core.ports;

import java.util.List;
import java.util.Optional;

import com.jbkloh.marieeanne.core.models.ProdutoLoja;

public interface ProdutoLojaRepositoryPort {

    Optional<ProdutoLoja> findById(Long idProduto);
    Optional<ProdutoLoja>findByLojaIdAndProdutoId(Long idLoja,Long idProduto);
    ProdutoLoja save(ProdutoLoja produtoLoja);
    List<ProdutoLoja> listarProdutosDisponiveisComEstoque(Long idLoja);
    List<ProdutoLoja> findAll();
    
    
}
