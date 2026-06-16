package com.jbkloh.marieeanne.infra.adpters;

import java.util.Optional;

import org.springframework.stereotype.Component;

import com.jbkloh.marieeanne.core.models.Cliente;
import com.jbkloh.marieeanne.core.ports.ClienteRepositoryPort;
import com.jbkloh.marieeanne.infra.mappers.ClienteMapper;
import com.jbkloh.marieeanne.infra.mappers.EnderecoMapper;
import com.jbkloh.marieeanne.infra.persistence.ClienteJpaRepository;
import com.jbkloh.marieeanne.infra.persistence.UserRoleJpaRepository;

import lombok.AllArgsConstructor;

@Component
@AllArgsConstructor
public class ClienteAdapter implements ClienteRepositoryPort {

    private final ClienteJpaRepository clienteJpaRepository;  
    private final UserRoleJpaRepository userRoleJpaRepository;

    @Override
    public void save(Cliente cliente) {
        clienteJpaRepository.save(ClienteMapper.toEntity(cliente,userRoleJpaRepository));
    }

    @Override
    public Optional<Cliente> findById(Long id) {
        return clienteJpaRepository.findById(id)
                .map(ClienteMapper::toDomain);
    }

    @Override
    public void deleteById(Long id) {
        clienteJpaRepository.deleteById(id);
    }

   @Override
    public void update(Cliente cliente) {
        clienteJpaRepository.findById(cliente.getId()).ifPresent(entityExistente -> {
        entityExistente.setNome(cliente.getNome());
        entityExistente.setTelefone(cliente.getTelefone());
        entityExistente.setEndereco(EnderecoMapper.toEntity(cliente.getEndereco()));
    
        clienteJpaRepository.save(entityExistente);
    });
}

    @Override
    public Optional<Cliente> findByUserId(Long userId) {
        return clienteJpaRepository.findByUsuarioId(userId)
        .map(ClienteMapper::toDomain);
    }
}