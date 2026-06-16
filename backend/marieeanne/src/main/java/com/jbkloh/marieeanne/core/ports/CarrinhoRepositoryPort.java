package com.jbkloh.marieeanne.core.ports;

import java.math.BigDecimal;
import java.util.List;

import com.jbkloh.marieeanne.core.models.Carrinho;
import com.jbkloh.marieeanne.core.models.ItemCarrinho;


public interface CarrinhoRepositoryPort {
    Carrinho findById(Long carrinhoId);
    Carrinho findByUserEmail(String email);
    Carrinho criarCarrinho(Long clienteId);
    Carrinho addProduto(List<ItemCarrinho> item, Long idCarrinho);
    void addCliente(Long clienteId, Carrinho carrinho);
    void removeProduto(Long idProduto, Carrinho idCarrinho);
    void limparProdutosCarrinho(Long idCarrinho);
    BigDecimal calcularTotal(Carrinho carrinhoDomain);
    Carrinho findCarrinhoByUserId(Long idCliente);
    Carrinho save(Carrinho carrinho);
    
}
