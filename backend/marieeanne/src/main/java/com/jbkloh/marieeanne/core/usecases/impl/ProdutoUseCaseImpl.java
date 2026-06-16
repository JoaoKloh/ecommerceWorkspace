package com.jbkloh.marieeanne.core.usecases.impl;

import java.math.BigDecimal;
import java.util.List;

import com.jbkloh.marieeanne.core.models.Produto;
import com.jbkloh.marieeanne.core.ports.ProdutoRepositoryPort;
import com.jbkloh.marieeanne.core.usecases.ProdutoUseCase;

public class ProdutoUseCaseImpl implements ProdutoUseCase {

    private final ProdutoRepositoryPort produtoRepositoryPort;

    public ProdutoUseCaseImpl(ProdutoRepositoryPort produtoRepositoryPort) {
        this.produtoRepositoryPort = produtoRepositoryPort;
    }
    @Override
    public void criarProduto(Produto produto){
        validarProduto(produto);
        if(produtoRepositoryPort.existsByName(produto.getNome())){
                throw new IllegalStateException("Já existe um produto cadastrado com esse nome.");
        }
        produtoRepositoryPort.save(produto);
    
    }
    @Override
    public void deletarProduto(Long id){
        if(!produtoRepositoryPort.existsById(id)){
            throw new IllegalStateException("Não é possivel excluir um produto que não foi criado.");
        }
        produtoRepositoryPort.deleteById(id);

    }
    @Override
    public void alternarDisponibilidade(Long id, boolean status) {
        Produto produto = produtoRepositoryPort.findById(id);
        if (produto == null) {
            throw new IllegalArgumentException("Produto não encontrado.");
        }
        validarProduto(produto);
        produto.setEstaDisponivel(status);
        produtoRepositoryPort.save(produto);
    }
    @Override
    public List<Produto> buscarTodos(){
        return produtoRepositoryPort.findAll();
    }
    @Override
    public Produto buscarPorNome(String nome) {
        return produtoRepositoryPort.findByName(nome);
    }
    @Override
    public void save(Produto produto){
        validarProduto(produto);
        produtoRepositoryPort.save(produto);
    }
    @Override
    public Produto buscarPorID(Long id) {
        Produto produto = produtoRepositoryPort.findById(id);
        validarProduto(produto);
        return produto;
    }
    @Override
    public Produto saveProduto(Produto produto) {
        validarProduto(produto);
        return produtoRepositoryPort.saveProduto(produto);
    }
    private void validarProduto(Produto produto) {
        if(produto==null){
            throw new IllegalArgumentException("O produto não pode ser nulo.");
        }
        if(produto.getDescricao()==null||produto.getDescricao().isEmpty()){
            throw new IllegalArgumentException("O campo descrição deve ser preenchido.");
        }
        if(produto.getPreco().equals(BigDecimal.ZERO)||produto.getPreco()==null){
            throw new IllegalArgumentException("O preço inserido para o produto é inválido.");
        }
        if (produto.getNome() == null||produto.getNome().isEmpty()) {
            throw new IllegalArgumentException("O nome inserido para o produto é inválido.");
        }
        if (produto.getImagem()==null||produto.getImagem().isEmpty()) {
            throw new IllegalArgumentException("A imagem deve ser inserida para o produto.");
        } 
    }
}
