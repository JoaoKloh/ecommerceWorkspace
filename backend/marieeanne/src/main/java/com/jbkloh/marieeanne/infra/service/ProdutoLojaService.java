package com.jbkloh.marieeanne.infra.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.jbkloh.marieeanne.core.models.Loja;
import com.jbkloh.marieeanne.core.models.Produto;
import com.jbkloh.marieeanne.core.models.ProdutoLoja;
import com.jbkloh.marieeanne.core.usecases.LojaUseCase;
import com.jbkloh.marieeanne.core.usecases.ProdutoLojaUseCase;
import com.jbkloh.marieeanne.core.usecases.ProdutoUseCase;
import com.jbkloh.marieeanne.infra.dtos.produto.ProdutoLojaAdminResponse;
import com.jbkloh.marieeanne.infra.dtos.produto.ProdutoRequestDTO;
import com.jbkloh.marieeanne.infra.exceptions.AppException;


@Service
public class ProdutoLojaService {
    @Autowired
    private ProdutoLojaUseCase produtoLojaUseCase;
    @Autowired
    private LojaUseCase lojaUseCase;
    @Autowired
    private ProdutoUseCase produtoUseCase;

     @Transactional
     public void atualizarEstoque(Long idProduto, Integer quantidade){
        produtoLojaUseCase.atualizarEstoqueProduto(idProduto,quantidade);
     }

    @Transactional
    public ProdutoLoja criarProduto(Long idLoja, ProdutoRequestDTO req, MultipartFile imagem) {
        String urlImagem = "https://seu-storage.com/marie-anne/produtos/" + imagem.getOriginalFilename(); 

        Loja loja = lojaUseCase.findById(idLoja);
        if (loja == null) {
            throw new AppException("Não é possível cadastrar o produto: Loja não encontrada.", HttpStatus.BAD_REQUEST);
        }

        Produto produto = new Produto();
        produto.setNome(req.nome());
        produto.setPreco(req.preco());
        produto.setDescricao(req.descricao());
        produto.setCategoria(req.categoria());
        produto.setDesconto(req.desconto() == null ? java.math.BigDecimal.ZERO : req.desconto());
        produto.setEstaDisponivel(req.estaDisponivel());
        produto.setImagem(urlImagem); 
    
        Produto produtoSalvo = produtoUseCase.saveProduto(produto);

        ProdutoLoja produtoLoja = new ProdutoLoja();
        produtoLoja.setProduto(produtoSalvo);
        produtoLoja.setLoja(loja); 
        produtoLoja.setQuantidade(req.estoque());

        return produtoLojaUseCase.save(produtoLoja);
    }
    @Transactional(readOnly=true)
    public List<ProdutoLojaAdminResponse> findAllAdmin(){

        List<ProdutoLoja> produtos = produtoLojaUseCase.findAll();
        
        return produtos.stream().map(p-> new ProdutoLojaAdminResponse(
                p.getId(),
                p.getProduto().getNome(),
                p.getProduto().getImagem(),
                p.getProduto().getCategoria(),
                p.getQuantidade(),
                p.getProduto().getPrecoComDesconto(),
                p.getProduto().getEstaDisponivel()
            )
        ).collect(Collectors.toList());
           
    }
    
}
