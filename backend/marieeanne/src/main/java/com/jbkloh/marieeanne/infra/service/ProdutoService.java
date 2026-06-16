package com.jbkloh.marieeanne.infra.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.jbkloh.marieeanne.core.models.Produto;
import com.jbkloh.marieeanne.core.models.ProdutoLoja;
import com.jbkloh.marieeanne.core.ports.ImagemRepositoryPort;
import com.jbkloh.marieeanne.core.usecases.ProdutoLojaUseCase;
import com.jbkloh.marieeanne.core.usecases.ProdutoUseCase;
import com.jbkloh.marieeanne.infra.dtos.endereco.EnderecoResponseDto;
import com.jbkloh.marieeanne.infra.dtos.produto.ProdutoCardapioResponse;
import com.jbkloh.marieeanne.infra.dtos.produto.ProdutoRequestDTO;
import com.jbkloh.marieeanne.infra.exceptions.AppException;
import com.jbkloh.marieeanne.infra.persistence.ItemCarrinhoJpaRepository;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class ProdutoService {
    @Autowired
    private ProdutoUseCase produtoUseCase;
    @Autowired
    private ImagemRepositoryPort imageStoragePort;
    @Autowired
    private ItemCarrinhoJpaRepository itemCarrinhoRepository;
    @Autowired
    private ProdutoLojaUseCase produtoLojaUseCase;

    @Transactional
    public Produto cadastrarNovoProduto(Produto produto,byte[] imagemBytes, String nomeArquivo) {
        
        inserirImagem(produto, imagemBytes, nomeArquivo);
        
        produtoUseCase.criarProduto(produto);
        log.info("armazenando a imagem");
        
        return produto;
    }

    @Transactional
    public Produto inserirImagem(Produto produto, byte[] imagemBytes, String nomeArquivo){
        String urlImagem = imageStoragePort.upload(imagemBytes,nomeArquivo);
        log.info("realizando o upload da imagem");
        
        produto.setImagem(urlImagem);
        log.info("setando a imagem no modelo de dados");
        return produto;
    }

    @Transactional
    public void alternarDisponibilidade(Long id, boolean status) {
        produtoUseCase.alternarDisponibilidade(id, status);
    }
    @Transactional
    public void atualizarProduto(ProdutoRequestDTO dto,byte[] imagemBytes, String nomeArquivo, Long id){
        System.out.println("id do produto é o SEGUINTE PORRA: "+id);
        ProdutoLoja produto = produtoLojaUseCase.findById(id);
        if(imagemBytes!=null){
            inserirImagem(produto.getProduto(), imagemBytes, nomeArquivo);

        }
        produto.getProduto().setNome(dto.nome());
        produto.getProduto().setDescricao(dto.descricao());
        produto.getProduto().setPreco(dto.preco());
        produto.getProduto().setCategoria(dto.categoria());
        produto.setQuantidade(dto.estoque());
        produto.getProduto().setEstaDisponivel(dto.estaDisponivel());
        produtoLojaUseCase.save(produto);
    }

    @Transactional(readOnly=true)
    public Produto findById(Long idProduto){
        return produtoUseCase.buscarPorID(idProduto);
    }

    @Transactional(readOnly = true)
    public List<ProdutoCardapioResponse> listarCardapioAtivo(Long idLoja) {
        return produtoLojaUseCase.listarProdutosDisponiveisComEstoque(idLoja)
                .stream()
                .map(p -> new ProdutoCardapioResponse(
                        p.getId(),
                        p.getProduto().getNome(),
                        p.getProduto().getDescricao(),
                        p.getProduto().getCategoria(), 
                        p.getProduto().getImagem(),
                        p.getQuantidade(),
                        p.getProduto().getPreco(),
                        p.getProduto().getDesconto(),
                        p.getProduto().getPrecoComDesconto(),
                        p.getProduto().getEstaDisponivel(),
                        new EnderecoResponseDto(
                                p.getLoja().getEndereco().getId(), 
                                p.getLoja().getEndereco().getRua(),
                                p.getLoja().getEndereco().getNumero(), 
                                p.getLoja().getEndereco().getComplemento(), 
                                p.getLoja().getEndereco().getBairro(), 
                                p.getLoja().getEndereco().getCidade(), 
                                p.getLoja().getEndereco().getEstado(), 
                                p.getLoja().getEndereco().getCep())
                ))
                .toList();
    }

    @Transactional(readOnly = true)
    public List<Produto> buscarTodos(){
        return produtoUseCase.buscarTodos();
    }
    @Transactional
    public void deletar(Long id) {
        itemCarrinhoRepository.deleteById(id);
        produtoUseCase.deletarProduto(id);
    }
    @Transactional
    public Produto buscarPorNome(String nome){
        return produtoUseCase.buscarPorNome(nome);
    }
}