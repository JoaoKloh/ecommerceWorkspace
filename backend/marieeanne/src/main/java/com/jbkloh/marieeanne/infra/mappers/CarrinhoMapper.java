package com.jbkloh.marieeanne.infra.mappers;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.stereotype.Component;

import com.jbkloh.marieeanne.core.models.Carrinho;
import com.jbkloh.marieeanne.core.models.ItemCarrinho;
import com.jbkloh.marieeanne.core.models.ProdutoLoja;
import com.jbkloh.marieeanne.infra.models.CarrinhoEntity;
import com.jbkloh.marieeanne.infra.models.ItemCarrinhoEntity;
import com.jbkloh.marieeanne.infra.models.ProdutoLojaEntity;
import com.jbkloh.marieeanne.infra.persistence.UserRoleJpaRepository;

import lombok.RequiredArgsConstructor;


@Component
@RequiredArgsConstructor
public class CarrinhoMapper {

    private final UserRoleJpaRepository userRoleJpaRepository;
    
    public List<ItemCarrinhoEntity> ItemCarrinhoDomainToEntity(List<ItemCarrinho> itemCarrinhos){
        if (itemCarrinhos==null){
            return null;
        }
        return itemCarrinhos.stream().map(this::toEntity).collect(Collectors.toList());
    }

    public  ItemCarrinho toDomain(ItemCarrinhoEntity entity) {
        if (entity == null){
            return null;
        }    
        ItemCarrinho item = new ItemCarrinho(entity.getId(),entity.getPrecoUnitario(),entity.getQuantidade());
        return item;
    
    }
    public  List<ItemCarrinho> ItemCarrinhoEntityToDOmain(List<ItemCarrinhoEntity> itens) {
        if (itens==null){
            return null;
        }
        return itens.stream().map(this::toDomain).collect(Collectors.toList());
    }
    public ItemCarrinhoEntity toEntity(ItemCarrinho itemCarrinho) {
        ItemCarrinhoEntity item = new ItemCarrinhoEntity();
        item.setId(itemCarrinho.getId());
        item.setPrecoUnitario(itemCarrinho.getPrecoUnitario());
        item.setQuantidade(itemCarrinho.getQuantidade());

        if (itemCarrinho.getProduto() != null) {
            ProdutoLojaEntity produtoEntity = new ProdutoLojaEntity();
            produtoEntity.setId(itemCarrinho.getProduto().getId()); 
            item.setProduto(produtoEntity); 
        }
        return item;
    }   
    public Carrinho CarrinhoEntityToDomain(CarrinhoEntity entity) {
        Carrinho domain = new Carrinho();
        domain.setId(entity.getId());
        domain.setValorTotal(entity.getValorTotal());
        domain.setCliente(UsuarioMapper.toDomain(entity.getUser())); 
    
        if (entity.getItens() != null) {
        domain.setProdutos(entity.getItens().stream()
            .map(itemEntity -> itemToDomain(itemEntity, domain)) 
            .toList());
        }
        return domain;
    }
    public ItemCarrinho itemToDomain(ItemCarrinhoEntity entity) {
        ProdutoLoja produtoDomain = ProdutoLojaMapper.toDomain(entity.getProduto());
        
        ItemCarrinho item = new ItemCarrinho();
        item.setProduto(produtoDomain);
        item.setQuantidade(entity.getQuantidade());
        
        item.setPrecoUnitario(produtoDomain.getProduto().getPreco()); 
        
        return item;
    }
    public CarrinhoEntity CarrinhoDomainToEntity(Carrinho carrinho){
        CarrinhoEntity carrinhoEntity = new CarrinhoEntity();
        carrinhoEntity.setId(carrinho.getId());
        carrinhoEntity.setValorTotal(carrinho.getValorTotal());
        carrinhoEntity.setUser(UsuarioMapper.toEntity(carrinho.getCliente(),userRoleJpaRepository));
        return carrinhoEntity;

    }
    public Optional<Carrinho> OptionalCarrinhoEntityToDomain(Optional<CarrinhoEntity> carrinhoEntity){
        Optional<Carrinho> carrinhoDomain= carrinhoEntity.stream().map(this::CarrinhoEntityToDomain).findFirst();
        return carrinhoDomain;
    }

    private ItemCarrinho itemToDomain(ItemCarrinhoEntity itemCarrinhoEntity, Carrinho carrinho){
        if (itemCarrinhoEntity == null){
            return null;
        }
        ItemCarrinho itemdomain = new ItemCarrinho();
        itemdomain.setId(itemCarrinhoEntity.getId());
        itemdomain.setQuantidade(itemCarrinhoEntity.getQuantidade());
        itemdomain.setPrecoUnitario(itemCarrinhoEntity.getPrecoUnitario());
        itemdomain.setProduto(ProdutoLojaMapper.toDomain(itemCarrinhoEntity.getProduto()));
        itemdomain.setCarrinho(carrinho);
        return itemdomain;
    }
   }
   