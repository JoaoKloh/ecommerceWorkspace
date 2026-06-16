package com.jbkloh.marieeanne.infra.adpters;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import com.jbkloh.marieeanne.core.models.Carrinho;
import com.jbkloh.marieeanne.core.models.ItemCarrinho;
import com.jbkloh.marieeanne.core.ports.CarrinhoRepositoryPort;
import com.jbkloh.marieeanne.infra.exceptions.AppException;
import com.jbkloh.marieeanne.infra.mappers.CarrinhoMapper;
import com.jbkloh.marieeanne.infra.models.CarrinhoEntity;
import com.jbkloh.marieeanne.infra.models.ItemCarrinhoEntity;
import com.jbkloh.marieeanne.infra.models.UserEntity;
import com.jbkloh.marieeanne.infra.persistence.CarrinhoJpaRepository;
import com.jbkloh.marieeanne.infra.persistence.UserJpaRepository;

import lombok.AllArgsConstructor;


@Component
@AllArgsConstructor
public class CarrinhoAdpter implements CarrinhoRepositoryPort{

    private final CarrinhoJpaRepository carrinhoJpaRepository;
    private final UserJpaRepository userJpaRepository;
    private final CarrinhoMapper carrinhoMapper;

    @Override
    public Carrinho findCarrinhoByUserId(Long idCliente) {
        return carrinhoJpaRepository.findCarrinhoByUserId(idCliente)
        .map( carrinhoEntity -> carrinhoMapper.CarrinhoEntityToDomain(carrinhoEntity))
        .orElseThrow(()-> new AppException("Não foi possível localizar nenhum carrinho do cliente com id:" +idCliente, HttpStatus.NOT_FOUND));
    }
    @Override
    public Carrinho findByUserEmail(String email) {
        return carrinhoJpaRepository.findCarrinhoByUserEmail(email)
        .map(carrinhoMapper::CarrinhoEntityToDomain)
        .orElse(null); 
    }
    @Override
    public void removeProduto(Long idProduto, Carrinho idCarrinho) {
        CarrinhoEntity carrinhoEntity = carrinhoJpaRepository.findById(carrinhoMapper.CarrinhoDomainToEntity(idCarrinho).getId())
        .orElseThrow(()-> new AppException("Não foi possivel localizar nenhum carrinho com o id informado", HttpStatus.NOT_FOUND));        
        carrinhoEntity.getItens().removeIf(p->p.getProduto().getId().equals(idProduto));
        carrinhoJpaRepository.save(carrinhoEntity);
    }
    @Override
    public void limparProdutosCarrinho(Long carrinhoId) {
        CarrinhoEntity carrinho = this.carrinhoJpaRepository.findById(carrinhoId)
            .orElseThrow(() -> new AppException("Carrinho não encontrado com ID: " + carrinhoId, HttpStatus.NOT_FOUND));
        
        carrinho.getItens().clear();
        carrinho.setValorTotal(BigDecimal.ZERO);
        this.carrinhoJpaRepository.save(carrinho);
    }
    @Override
    public BigDecimal calcularTotal(Carrinho carrinhoDomain) {
        CarrinhoEntity carrinhoEntity = this.carrinhoJpaRepository.findById(carrinhoDomain.getId())
            .orElseThrow(() -> new AppException("Carrinho não encontrado.", HttpStatus.NOT_FOUND));
        
        BigDecimal valorCalculado = carrinhoDomain.getValorTotal(); 
        
        carrinhoEntity.setValorTotal(valorCalculado);
        this.carrinhoJpaRepository.save(carrinhoEntity);
        
        return valorCalculado;
    }
    @Override
    public Carrinho addProduto(List<ItemCarrinho> itemCore, Long idCarrinho) {
        CarrinhoEntity carrinhoEntity = this.carrinhoJpaRepository.findById(idCarrinho)
        .orElseThrow(()->new AppException("Carrinho não encontrado.", HttpStatus.NOT_FOUND));

        List<ItemCarrinhoEntity>itens = carrinhoMapper.ItemCarrinhoDomainToEntity(itemCore);
        carrinhoEntity.getItens().clear();
        itens.forEach(item -> {
            item.setCarrinho(carrinhoEntity);
            carrinhoEntity.getItens().add(item);});
        CarrinhoEntity salvo = carrinhoJpaRepository.save(carrinhoEntity);
    
        return carrinhoMapper.CarrinhoEntityToDomain(salvo);       
        
    }
    @Override
    public void addCliente(Long clienteId, Carrinho carrinhoId) {
        CarrinhoEntity carrinho = this.carrinhoJpaRepository.findById(carrinhoId.getId())
            .orElseThrow(() -> new IllegalArgumentException("Carrinho não encontrado com ID: " + carrinhoId.getId()));
        UserEntity userEntity = this.userJpaRepository.findById(clienteId)
            .orElseThrow(() -> new IllegalArgumentException("Cliente não encontrado com ID: " + clienteId));

        carrinho.setUser(userEntity);
        this.carrinhoJpaRepository.save(carrinho);
    }
    @Override
    public Carrinho criarCarrinho(Long clienteId) {
        UserEntity userEntity = userJpaRepository.findById(clienteId)
        .orElseThrow(()->new AppException("Não foi possível localizar o cliente, favor cadastre-se no sistema.", HttpStatus.NOT_FOUND));

        CarrinhoEntity carrinhoEntity = new CarrinhoEntity();
        carrinhoEntity.setUser(userEntity);
        carrinhoEntity.setValorTotal(BigDecimal.ZERO);
        carrinhoJpaRepository.save(carrinhoEntity);

        return carrinhoMapper.CarrinhoEntityToDomain(carrinhoEntity);
    }
    @Override
    public Carrinho findById(Long carrinhoId) {
        return carrinhoJpaRepository.findById(carrinhoId) 
        .map( carrinhoEntity -> carrinhoMapper.CarrinhoEntityToDomain(carrinhoEntity))
        .orElseThrow(()-> new AppException("Não foi possível localizar nenhum carrinho com esse id:" +carrinhoId, HttpStatus.NOT_FOUND));
    }
    @Override
    public Carrinho save(Carrinho carrinho) {
        CarrinhoEntity carrinhoEntity = carrinhoJpaRepository.findById(carrinho.getId())
            .orElseThrow(() -> new AppException("Carrinho não encontrado.", HttpStatus.NOT_FOUND));
    
        carrinhoEntity.setValorTotal(carrinho.getValorTotal());
    
        // 1. Garantia contra NullPointerException: 
        // Se a lista estiver null, inicializamos como uma nova lista
        if (carrinhoEntity.getItens() == null) {
            carrinhoEntity.setItens(new ArrayList<>());
        } else {
            // Se já existirem itens, limpamos para evitar duplicatas 
            // e permitir que o orphanRemoval remova do banco o que saiu
            carrinhoEntity.getItens().clear();
        }
    
        // 2. Mapeamento dos itens do Domínio para a Entidade
        List<ItemCarrinhoEntity> novosItensEntity = carrinhoMapper.ItemCarrinhoDomainToEntity(carrinho.getProdutos());
    
        // 3. Adição segura vinculando o relacionamento bidirecional
        if (novosItensEntity != null) {
            novosItensEntity.forEach(item -> {
                item.setCarrinho(carrinhoEntity); // Vínculo essencial para a FK
                carrinhoEntity.getItens().add(item);
            });
        }
    
        CarrinhoEntity salvo = carrinhoJpaRepository.save(carrinhoEntity);
    
        return carrinhoMapper.CarrinhoEntityToDomain(salvo);
    }
}
