package com.jbkloh.marieeanne.core.usecases;
import java.math.BigDecimal;

import com.jbkloh.marieeanne.core.models.Carrinho;
import com.jbkloh.marieeanne.core.models.Usuario;


public interface  CarrinhoUseCase {
Carrinho buscarPorUsuarioId(Long clienteId);
Carrinho buscarPorEmailUsuario(String email);
Carrinho criarCarrinho(Usuario clienteId);
Carrinho adicionarProdutoAoCarrinho(Carrinho carrinho, Long produtoID, int quantidade);
void removerProdutoDoCarrinho(Carrinho carrinhoid, Long itemid);
void limparCarrinho(Carrinho carrinho);
BigDecimal calcularTotalCarrinho(Carrinho carrinho);
Carrinho obterCarrinhoPorId(Long id);
Carrinho removerUnidadeDeItens(Carrinho carrinho,Long produtoId,int quantidade);

}
  