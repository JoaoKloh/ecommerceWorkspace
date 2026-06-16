package com.jbkloh.marieeanne.infra.service;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.jbkloh.marieeanne.core.models.Carrinho;
import com.jbkloh.marieeanne.core.ports.AutenticacaoPort;
import com.jbkloh.marieeanne.core.usecases.CarrinhoUseCase;
import com.jbkloh.marieeanne.infra.dtos.carrinho.CarrinhoResponseDTO;
import com.jbkloh.marieeanne.infra.dtos.itemCarrinho.itemCarrinhoResponseDTO;
import com.jbkloh.marieeanne.infra.exceptions.AppException;

import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class CarrinhoService {
    @Autowired
    private final CarrinhoUseCase carrinhoUseCase;
    @Autowired
    private final AutenticacaoPort autenticacaoPort;
    
    @Transactional()
    public Carrinho obterCarrinhoDoCliente() {
        String emailUsarioAutenticado = autenticacaoPort.getEmailUsuarioLogado();

        if(emailUsarioAutenticado == null||emailUsarioAutenticado.isEmpty()){
            throw new AppException("O usuário não está autenticado", HttpStatus.UNAUTHORIZED);
        }
        Carrinho carrinho = carrinhoUseCase.buscarPorEmailUsuario(emailUsarioAutenticado);
        carrinhoUseCase.calcularTotalCarrinho(carrinho);
        return carrinho;
    }
    @Transactional
    public CarrinhoResponseDTO adicionarItem( Long produtoId, int quantidade) {
        String emailUsarioAutenticado = autenticacaoPort.getEmailUsuarioLogado();
        Carrinho carrinho = carrinhoUseCase.buscarPorEmailUsuario(emailUsarioAutenticado);
        Carrinho carrinhoAtualizado = carrinhoUseCase.adicionarProdutoAoCarrinho(carrinho, produtoId, quantidade);
        List<itemCarrinhoResponseDTO> itensDTO = carrinhoAtualizado.getProdutos().stream()
        .map(item -> new itemCarrinhoResponseDTO(
            item.getProduto().getId(),
            item.getProduto().getProduto().getNome(),
            item.getQuantidade(),
            item.getPrecoUnitario(), 
            carrinhoAtualizado.getValorTotal())        
        )
        .toList();
        return new CarrinhoResponseDTO(
        itensDTO,
        carrinhoUseCase.calcularTotalCarrinho(carrinhoAtualizado)
    );
    }
    @Transactional
    public void removerItem(Long produtoId) {
        String emailUsarioAutenticado = autenticacaoPort.getEmailUsuarioLogado();
        Carrinho carrinho = carrinhoUseCase.buscarPorEmailUsuario(emailUsarioAutenticado);

        carrinhoUseCase.removerProdutoDoCarrinho(carrinho, produtoId);
    }
    @Transactional
    public void esvaziarCarrinho() {
        String emailUsarioAutenticado = autenticacaoPort.getEmailUsuarioLogado();
        Carrinho carrinho = carrinhoUseCase.buscarPorEmailUsuario(emailUsarioAutenticado);
        carrinhoUseCase.limparCarrinho(carrinho);
    }
    @Transactional
    public CarrinhoResponseDTO removerUnidadeItem(Long produtoId, int quantidade){
        String emailUsarioAutenticado = autenticacaoPort.getEmailUsuarioLogado();
        Carrinho carrinho = carrinhoUseCase.buscarPorEmailUsuario(emailUsarioAutenticado);
        Carrinho carrinhoAtualizado = carrinhoUseCase.removerUnidadeDeItens(carrinho, produtoId, quantidade);
        List<itemCarrinhoResponseDTO> itensDTO = carrinhoAtualizado.getProdutos().stream()
        .map(item -> new itemCarrinhoResponseDTO(
            item.getProduto().getId(),
            item.getProduto().getProduto().getNome(),
            item.getQuantidade(),
            item.getPrecoUnitario(), 
            carrinhoAtualizado.getValorTotal()
        ))
        .toList();
        return new CarrinhoResponseDTO(
        itensDTO,
        carrinhoUseCase.calcularTotalCarrinho(carrinhoAtualizado)
    );
    }

    @Transactional(readOnly = true)
    public BigDecimal verTotal() {
        String emailUsarioAutenticado = autenticacaoPort.getEmailUsuarioLogado();
        Carrinho carrinho = carrinhoUseCase.buscarPorEmailUsuario(emailUsarioAutenticado);
        return carrinhoUseCase.calcularTotalCarrinho(carrinho);
    }

    @Transactional(readOnly = true)
    public Carrinho obterCarrinhoById(Long idCarrinho){
        return carrinhoUseCase.obterCarrinhoPorId(idCarrinho);
    }
}