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

        return new Cliente(
            entity.getId(),
            entity.getNome(),
            entity.getCpf(),
            entity.getTelefone(),
            EnderecoMapper.toDomain(entity.getEndereco()),
            entity.getDataNascimento(),
            UsuarioMapper.toDomain(entity.getUsuario())
        );
    }
}