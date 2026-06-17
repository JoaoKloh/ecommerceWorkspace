package com.jbkloh.marieeanne.infra.mappers;

import com.jbkloh.marieeanne.core.models.Cliente;
import com.jbkloh.marieeanne.infra.models.ClienteEntity;
import com.jbkloh.marieeanne.infra.persistence.UserRoleJpaRepository;

public class ClienteMapper {

    public static ClienteEntity toEntity(Cliente domain, UserRoleJpaRepository userRoleJpaRepository) {
        if (domain == null) return null;

        ClienteEntity entity = new ClienteEntity();
        entity.setId(domain.getId());
        entity.setNome(domain.getNome());
        entity.setTelefone(domain.getTelefone());
        entity.setEndereco(EnderecoMapper.toEntity(domain.getEndereco()));
        entity.setDataNascimento(domain.getDataNascimento());
        entity.setCpf(domain.getCpf());
        
        entity.setUsuario(UsuarioMapper.toEntity(domain.getUser(), userRoleJpaRepository));
        
        return entity;
    }

    public static Cliente toDomain(ClienteEntity entity) {
        if (entity == null) return null;

        Cliente cliente = new Cliente();
        cliente.setId(entity.getId());
        cliente.setCpf(entity.getCpf());
        cliente.setDataNascimento(entity.getDataNascimento());  
        cliente.setEndereco(EnderecoMapper.toDomain(entity.getEndereco()));
        cliente.setUser(UsuarioMapper.toDomain(entity.getUsuario()));
        cliente.setTelefone(entity.getTelefone());
        cliente.setNome(entity.getNome());
        return cliente;

    }
}