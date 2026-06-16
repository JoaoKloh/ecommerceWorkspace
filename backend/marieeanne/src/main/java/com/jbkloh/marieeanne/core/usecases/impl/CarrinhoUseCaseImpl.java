package com.jbkloh.marieeanne.core.usecases.impl;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Optional;

import com.jbkloh.marieeanne.core.models.Carrinho;
import com.jbkloh.marieeanne.core.models.ItemCarrinho;
import com.jbkloh.marieeanne.core.models.ProdutoLoja;
import com.jbkloh.marieeanne.core.models.Usuario;
import com.jbkloh.marieeanne.core.ports.AutenticacaoPort;
import com.jbkloh.marieeanne.core.ports.CarrinhoRepositoryPort;
import com.jbkloh.marieeanne.core.ports.ProdutoLojaRepositoryPort;
import com.jbkloh.marieeanne.core.ports.UsuarioRepositoryPort;
import com.jbkloh.marieeanne.core.usecases.CarrinhoUseCase;

public class CarrinhoUseCaseImpl implements CarrinhoUseCase {
    private final CarrinhoRepositoryPort carrinhoRepositoryPort;
    private final AutenticacaoPort autenticacaoPort;
    private final UsuarioRepositoryPort usuarioRepositoryPort;
    private final ProdutoLojaRepositoryPort produtoLojaRepositoryPort;

    public CarrinhoUseCaseImpl(CarrinhoRepositoryPort carrinhoRepositoryPort, AutenticacaoPort autenticacaoPort, ProdutoLojaRepositoryPort produtoLojaRepositoryPort1,UsuarioRepositoryPort usuarioRepositoryPort) {
        this.carrinhoRepositoryPort = carrinhoRepositoryPort;
        this.autenticacaoPort = autenticacaoPort;
        this.usuarioRepositoryPort = usuarioRepositoryPort;
        this.produtoLojaRepositoryPort = produtoLojaRepositoryPort1;
    }
    @Override
    public Carrinho removerUnidadeDeItens(Carrinho carrinho, Long produtoId,int quantidade) {
        validarAcesso(carrinho);
        ItemCarrinho item = carrinho.getProdutos().stream()
        .filter(p -> p.getProduto().getId().equals(produtoId))
        .findFirst()
        .orElseThrow(() -> new IllegalArgumentException("O produto não existe no carrinho"));
        
        int novaQuantidade = item.getQuantidade() - quantidade;
        if (novaQuantidade == 0) {
            carrinhoRepositoryPort.removeProduto(produtoId, carrinho);
            return carrinhoRepositoryPort.save(carrinho);
        }
        else{
            item.setQuantidade(novaQuantidade);
            validarItemCarrinho(item);
        }
        return carrinhoRepositoryPort.save(carrinho);
    }
    @Override
    public Carrinho buscarPorEmailUsuario(String email) {
        Carrinho carrinho = carrinhoRepositoryPort.findByUserEmail(email);
        if(carrinho==null||carrinho.getCliente()==null){
            Usuario usuario = usuarioRepositoryPort.findByUsuarioEmail(email)
            .orElseThrow(()->new IllegalArgumentException("Usuário não encontrado"));
            
            return carrinhoRepositoryPort.criarCarrinho(usuario.getId());
        }
        validarAcesso(carrinho);
        return carrinho;
    }
    @Override
    public Carrinho buscarPorUsuarioId(Long clienteId) {
        Carrinho carrinho = carrinhoRepositoryPort.findCarrinhoByUserId(clienteId);
        if (carrinho == null) {
            throw new RuntimeException("Carrinho não encontrado para o cliente informado.");
        }
        return carrinho;
    }
    @Override
    public Carrinho criarCarrinho(Usuario cliente) {
        if (cliente == null || !autenticacaoPort.estaAutenticado()) {
            throw new RuntimeException("Usuário precisa estar autenticado para criar um carrinho.");
        }
        return carrinhoRepositoryPort.criarCarrinho(cliente.getId());
    }
    @Override
    public Carrinho adicionarProdutoAoCarrinho(Carrinho carrinho, Long produtoID, int quantidade) {
        validarAcesso(carrinho); 

        System.out.println("Adicionando produto ao carrinho: Produto ID = " + produtoID + ", Quantidade = " + quantidade);
        System.out.println("Carrinho atual: " + (carrinho.getProdutos() != null ? carrinho.getProdutos().size() : "Nenhum produto"));
        System.out.println("Quantidade atual do produto no carrinho: " + carrinho.getProdutos().stream()
            .filter(p -> p.getProduto() != null && p.getProduto().getId().equals(produtoID))
            .map(ItemCarrinho::getQuantidade)
            .findFirst()
            .orElse(0));
    
        Optional<ProdutoLoja> produto = produtoLojaRepositoryPort.findById(produtoID);
        if (produto.isEmpty()){ 
            throw new RuntimeException("Produto não encontrado.");
        }
        ProdutoLoja produtoReal = produto.get();
        
        if (carrinho.getProdutos() == null) {
        carrinho.setProdutos(new ArrayList<>());
        } else {
        carrinho.setProdutos(new ArrayList<>(carrinho.getProdutos()));
        }

        Optional<ItemCarrinho> itemExistente = carrinho.getProdutos().stream()
            .filter(p -> p.getProduto().getId().equals(produtoReal.getId()))
            .findFirst();

        if (itemExistente.isPresent()) {
            ItemCarrinho item = itemExistente.get();
            if(item.getProduto()==null){
                item.setProduto(produtoReal);
            }
            item.setQuantidade(item.getQuantidade()+quantidade);
            System.out.println("Atualizando item do carrinho: " + item.getProduto().getProduto().getNome() + ", Quantidade: " + item.getQuantidade());
            validarItemCarrinho(item);

        }   
        else {
            ItemCarrinho novoItem = new ItemCarrinho(produtoReal, quantidade, produtoReal.getProduto().getPrecoComDesconto());
            novoItem.setCarrinho(carrinho);
            validarItemCarrinho(novoItem);
            carrinho.getProdutos().add(novoItem);
        }
        return carrinhoRepositoryPort.save(carrinho);
    }
    @Override
    public void removerProdutoDoCarrinho(Carrinho carrinho, Long produtoId) {
        validarAcesso(carrinho);

        ItemCarrinho item = carrinho.getProdutos().stream()
            .filter(p -> p.getProduto() != null && p.getProduto().getId().equals(produtoId))
            .findFirst()
            .orElseThrow(() -> new IllegalStateException("Item não encontrado no carrinho."));

        this.carrinhoRepositoryPort.removeProduto(item.getProduto().getId(), carrinho);
    }
    @Override
    public void limparCarrinho(Carrinho carrinho) {
        validarAcesso(carrinho);
        this.carrinhoRepositoryPort.limparProdutosCarrinho(carrinho.getId());
    }
    @Override
    public BigDecimal calcularTotalCarrinho(Carrinho carrinho) {
        validarAcesso(carrinho);
        return carrinhoRepositoryPort.calcularTotal(carrinho);
    }
    @Override
    public Carrinho obterCarrinhoPorId(Long id) {
        Carrinho carrinho = carrinhoRepositoryPort.findById(id);
        validarAcesso(carrinho);
        return carrinho;
    }
    
    private void validarAcesso(Carrinho carrinho) {
        if (carrinho == null) {
            throw new RuntimeException("Operação inválida: Carrinho inexistente.");
        }
        if (!autenticacaoPort.estaAutenticado()) {
            throw new RuntimeException("Acesso negado: Usuário não autenticado.");
        }
        
        String emailUsuarioAutenticado = autenticacaoPort.getEmailUsuarioLogado();
        if (!carrinho.getCliente().getEmail().equals(emailUsuarioAutenticado)) {
            throw new RuntimeException("Acesso negado: Este carrinho pertence a outro usuário.");
        }
    }

    private void validarItemCarrinho(ItemCarrinho item) {

        System.out.println("Validando item do carrinho: " + item.getProduto().getProduto().getNome() + ", Quantidade: " + item.getQuantidade());
        System.out.println("Produto disponível: " + item.getProduto().getProduto().getEstaDisponivel() );
        if (item == null || item.getProduto() == null) {
            throw new IllegalArgumentException("Dados do item inválidos.");
        }

        ProdutoLoja produto = item.getProduto();

        if (!produto.getProduto().getEstaDisponivel()) {
            throw new IllegalStateException("Produto indisponível: " + produto.getProduto().getNome());
        }

        if (item.getQuantidade() <= 0) {
            throw new IllegalArgumentException("A quantidade deve ser maior que zero.");
        }
    }
    

}