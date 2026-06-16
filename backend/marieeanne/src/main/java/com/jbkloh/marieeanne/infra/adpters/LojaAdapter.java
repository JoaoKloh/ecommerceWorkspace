package com.jbkloh.marieeanne.infra.adpters;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Component;

import com.jbkloh.marieeanne.core.models.Loja;
import com.jbkloh.marieeanne.core.ports.LojaRepositoryPort;
import com.jbkloh.marieeanne.infra.mappers.LojaMapper;
import com.jbkloh.marieeanne.infra.persistence.LojaJpaRepository;

import lombok.AllArgsConstructor;

@Component
@AllArgsConstructor
public class LojaAdapter implements LojaRepositoryPort {

    private final LojaJpaRepository lojaJpaRepository;

    @Override
    public Optional<Loja> findById(Long idLoja){
        return lojaJpaRepository.findById(idLoja).map(LojaMapper::toDomain);
    }

    @Override
    public void save(Loja loja) {
        lojaJpaRepository.save(LojaMapper.toEntity(loja));
    }

    @Override
    public void delete(Long id) {
        lojaJpaRepository.deleteById(id);
    }

    @Override
    public List<Loja> findAll() {
        return lojaJpaRepository.findAll().stream()
        .map(LojaMapper::toDomain)
        .toList();
    }
    
}
