package com.jbkloh.marieeanne.infra.adpters;

import org.springframework.stereotype.Component;

import com.jbkloh.marieeanne.core.models.Endereco;
import com.jbkloh.marieeanne.core.ports.EnderecoRepositoryPort;
import com.jbkloh.marieeanne.infra.mappers.EnderecoMapper;
import com.jbkloh.marieeanne.infra.persistence.EnderecoJpaRepository;

import lombok.AllArgsConstructor;

@AllArgsConstructor
@Component
public class EnderecoAdpter implements EnderecoRepositoryPort{

    private final EnderecoMapper enderecoMapper;
    private final EnderecoJpaRepository enderecoJpaRepository;

    @Override
    public Endereco save(Endereco endereco) {
        return enderecoMapper.toDomain(enderecoJpaRepository.save(enderecoMapper.toEntity(endereco)));
    }

    @Override
    public Endereco findById(Long id) {
        return enderecoMapper.toDomain(enderecoJpaRepository.findById(id).orElse(null));
    }

    @Override
    public void deleteById(Long id) {
        enderecoJpaRepository.deleteById(id);
    }
    
}
