package com.jbkloh.marieeanne.core.usecases.impl;

import java.util.List;
import java.util.Optional;

import com.jbkloh.marieeanne.core.models.ItemPedido;
import com.jbkloh.marieeanne.core.models.ProdutoLoja;
import com.jbkloh.marieeanne.core.ports.ProdutoLojaRepositoryPort;
import com.jbkloh.marieeanne.core.usecases.ProdutoLojaUseCase;

public class ProdutoLojaUseCaseImpl implements ProdutoLojaUseCase {

    private final ProdutoLojaRepositoryPort produtoLojaRepositoryPort;

    public ProdutoLojaUseCaseImpl(ProdutoLojaRepositoryPort produtoLojaRepositoryPort){
        this.produtoLojaRepositoryPort = produtoLojaRepositoryPort;
    }

    @Override
    public ProdutoLoja findById(Long idProduto) {
        Optional<ProdutoLoja> produto = produtoLojaRepositoryPort.findById(idProduto);
        if(produto.isEmpty()){
            throw new RuntimeException("Não existe produto com id inserido.");
        }
        return produto.get();
    }
    @Override
    public ProdutoLoja save(ProdutoLoja produtoLoja) {
        validarDados(produtoLoja);
        return produtoLojaRepositoryPort.save(produtoLoja);
    }
    @Override
    public void atualizarEstoquePedido(List<ItemPedido> itens) {
    if (itens == null || itens.isEmpty()) {
        throw new IllegalArgumentException("A lista de itens do pedido não pode estar vazia.");
    }

    for (ItemPedido item : itens) {
        ProdutoLoja produtoLoja = item.getProduto();
        
        if (produtoLoja == null) {
            throw new IllegalArgumentException("Item do pedido contém um produto inválido (nulo).");
        }

        Integer quantidadeVendida = item.getQuantidade();
        if (quantidadeVendida == null || quantidadeVendida <= 0) {
            throw new IllegalArgumentException("Quantidade inválida para o produto ID: " + produtoLoja.getId());
        }

        Integer estoqueAtual = produtoLoja.getQuantidade();
        if (estoqueAtual == null) {
            estoqueAtual = 0; // Evita NullPointerException caso o estoque esteja nulo no banco
        }

        // Como o webhook confirmou o pagamento, nós SUBTRAÍMOS do estoque
        Integer estoqueAtualizado = estoqueAtual - quantidadeVendida;

        if (estoqueAtualizado < 0) {
            throw new IllegalArgumentException("Estoque insuficiente para o produto ID " + produtoLoja.getId() 
                    + ". Estoque atual: " + estoqueAtual + ", Solicitado: " + quantidadeVendida);
        }

        produtoLoja.setQuantidade(estoqueAtualizado);
        produtoLojaRepositoryPort.save(produtoLoja);
        }
    }
    @Override
    public void atualizarEstoqueProduto(Long id, Integer qtd) {
        Optional<ProdutoLoja>produto=produtoLojaRepositoryPort.findById(id);

        if(produto.isEmpty()){
            throw new RuntimeException("Não existe produto com id inserido.");
        }
        ProdutoLoja produtoExistente = produto.get();
        Integer estoqueDesatualizado = produtoExistente.getQuantidade();
        Integer estoqueAtualizado = estoqueDesatualizado + qtd;

        produtoExistente.setQuantidade(estoqueAtualizado);
        produtoLojaRepositoryPort.save(produtoExistente);
    }
    @Override
    public List<ProdutoLoja> listarProdutosDisponiveisComEstoque(Long idLoja) {
        return produtoLojaRepositoryPort.listarProdutosDisponiveisComEstoque(idLoja);
    } 
    @Override
    public List<ProdutoLoja> findAll() {
        return produtoLojaRepositoryPort.findAll();
    }
    private void validarDados(ProdutoLoja produtoLoja) {
        if(produtoLoja == null){
            throw new IllegalArgumentException("Os dados de produto e loja não podem ser nulos.");
        }
        if (produtoLoja.getProduto() == null ) {
            throw new IllegalArgumentException("Verifique os dados inseridos do produto e tente novamente mais tarde.");
        }

        if (produtoLoja.getLoja() == null) {
            throw new IllegalArgumentException("Verifique as informações inseridas da loja e tente novamente mais tarde.");
        }

        if (produtoLoja.getQuantidade() == null || produtoLoja.getQuantidade() <0) {
            throw new IllegalArgumentException("O estoque inicial não pode ser inferior a zero.");
        }
    } 
}
