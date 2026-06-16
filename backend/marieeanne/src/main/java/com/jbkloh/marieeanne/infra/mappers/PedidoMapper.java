package com.jbkloh.marieeanne.infra.mappers;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Component;

import com.jbkloh.marieeanne.core.models.Cliente;
import com.jbkloh.marieeanne.core.models.Endereco;
import com.jbkloh.marieeanne.core.models.ItemPedido;
import com.jbkloh.marieeanne.core.models.Pagamento;
import com.jbkloh.marieeanne.core.models.Pedido;
import com.jbkloh.marieeanne.core.models.enums.PagamentoStatus;
import com.jbkloh.marieeanne.infra.models.ClienteEntity;
import com.jbkloh.marieeanne.infra.models.EnderecoEntity;
import com.jbkloh.marieeanne.infra.models.ItemPedidoEntity;
import com.jbkloh.marieeanne.infra.models.PagamentoEntity;
import com.jbkloh.marieeanne.infra.models.PedidoEntity;

@Component
public class PedidoMapper {

    public static PedidoEntity PedidoDomainToPedidoEntity(Pedido domain) {
        if (domain == null) {
            return null;
        }

        PedidoEntity entity = new PedidoEntity();
        entity.setId(domain.getId());
        entity.setStatus(domain.getStatus());
        entity.setValorTotal(domain.getValor());
        entity.setDataCriacao(domain.getDataCriacao() != null ? domain.getDataCriacao() : LocalDateTime.now());
        entity.setDataAtualizacao(LocalDateTime.now());
        entity.setLoja(LojaMapper.toEntity(domain.getLoja()));

        entity.setDataRetirada(domain.getDataRetirada());
        entity.setHoraRetirada(domain.getHoraRetirada());

        if (domain.getEndereco() != null) {
            EnderecoEntity enderecoEntity = new EnderecoEntity();
            enderecoEntity.setId(domain.getEndereco().getId());
            enderecoEntity.setRua(domain.getEndereco().getRua());
            enderecoEntity.setBairro(domain.getEndereco().getBairro());
            enderecoEntity.setCidade(domain.getEndereco().getCidade());
            enderecoEntity.setCep(domain.getEndereco().getCep());
            enderecoEntity.setCidade(domain.getEndereco().getCidade());
            enderecoEntity.setEstado(domain.getEndereco().getEstado());
            enderecoEntity.setComplemento(domain.getEndereco().getComplemento());
            entity.setEndereco(enderecoEntity);
        }

        if (domain.getCliente() != null) {
            ClienteEntity clienteEntity = new ClienteEntity();
            clienteEntity.setId(domain.getCliente().getId());
            clienteEntity.setCpf(domain.getCliente().getCpf());
            entity.setCliente(clienteEntity);
        }
        
        if (domain.getPagamento() != null) {
            PagamentoEntity pagamentoEntity = new PagamentoEntity();
            pagamentoEntity.setId(domain.getPagamento().getId());
            pagamentoEntity.setValor(domain.getPagamento().getValor());
            
            pagamentoEntity.setStatusPagamento("PENDENTE"); 
            pagamentoEntity.setPedido(entity); 

            entity.setPagamento(pagamentoEntity);
        }

        if (domain.getProdutos() != null) {
            List<ItemPedidoEntity> itemEntities = domain.getProdutos().stream()
                .map(item -> itemToEntity(item, entity))
                .collect(Collectors.toList());
            entity.setProdutos(itemEntities);
        }
        
        return entity;
    }

    public static Pedido EntityToDomain(PedidoEntity entity) {
        if (entity == null) return null;

        Pedido domain = new Pedido();
        domain.setId(entity.getId());
        domain.setStatus(entity.getStatus());
        domain.setValor(entity.getValorTotal());
        domain.setDataCriacao(entity.getDataCriacao());
        domain.setLoja(LojaMapper.toDomain(entity.getLoja()));
        
        domain.setDataRetirada(entity.getDataRetirada());
        domain.setHoraRetirada(entity.getHoraRetirada());

        if (entity.getPagamento() != null) {
            Pagamento pagamentoDomain = new Pagamento();
            pagamentoDomain.setId(entity.getPagamento().getId());
            pagamentoDomain.setValor(entity.getPagamento().getValor());
            pagamentoDomain.setTransacaoIdMercadoPago(entity.getPagamento().getTransacaoIdMercadoPago());
            
            if (entity.getPagamento().getStatusPagamento() != null) {
                pagamentoDomain.setStatusPagamento(PagamentoStatus.valueOf(entity.getPagamento().getStatusPagamento()));
            }
            
            domain.setPagamento(pagamentoDomain); 
        }

        if (entity.getProdutos() != null) {
            List<ItemPedido> itensDomain = entity.getProdutos().stream()
                .map(itemEntity -> itemToDomain(itemEntity, domain))
                .collect(Collectors.toList());
            domain.setProdutos(itensDomain);
        }

        if (entity.getCliente() != null) {
            Cliente cliente =ClienteMapper.toDomain(entity.getCliente());
            domain.setCliente(cliente);
            
        }
        if(entity.getEndereco()!=null){
            Endereco endereco = new Endereco();
            endereco.setId(entity.getEndereco().getId());
            endereco.setComplemento(entity.getEndereco().getComplemento());
            endereco.setNumero(entity.getEndereco().getNumero());
            endereco.setCep(entity.getEndereco().getCep());
            endereco.setEstado(entity.getEndereco().getEstado());
            endereco.setCidade(entity.getEndereco().getCidade());
            endereco.setRua(entity.getEndereco().getRua());
            endereco.setBairro(entity.getEndereco().getBairro());
            domain.setEndereco(endereco);
        }

        return domain;
    }

    private static ItemPedido itemToDomain(ItemPedidoEntity entity, Pedido domain) {
        ItemPedido itemDomain = new ItemPedido();
        itemDomain.setId(entity.getId());
        itemDomain.setPedido(domain);
        itemDomain.setPrecoUnitario(entity.getPreco());
        itemDomain.setProduto(ProdutoLojaMapper.toDomain(entity.getProduto()));
        itemDomain.setQuantidade(entity.getQuantidade());
        return itemDomain;
    }

    private static ItemPedidoEntity itemToEntity(ItemPedido domain, PedidoEntity pedidoEntity) {
        ItemPedidoEntity entity = new ItemPedidoEntity();
        entity.setId(domain.getId());
        entity.setProduto(ProdutoLojaMapper.toEntity(domain.getProduto())); 
        entity.setQuantidade(domain.getQuantidade());
        entity.setPreco(domain.getPrecoUnitario());
        entity.setPedido(pedidoEntity); 
        return entity;
    }
}