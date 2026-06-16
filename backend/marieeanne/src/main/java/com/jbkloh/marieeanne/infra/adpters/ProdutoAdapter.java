package com.jbkloh.marieeanne.infra.adpters;

import java.util.List;

import org.springframework.stereotype.Component;

import com.jbkloh.marieeanne.core.models.Produto;
import com.jbkloh.marieeanne.core.ports.ProdutoRepositoryPort;
import com.jbkloh.marieeanne.infra.mappers.ProdutoMapper; 
import com.jbkloh.marieeanne.infra.models.ProdutoEntity;
import com.jbkloh.marieeanne.infra.persistence.ProdutoJpaRepository;

import lombok.AllArgsConstructor;

@Component
@AllArgsConstructor
public class ProdutoAdapter implements ProdutoRepositoryPort {

    private final ProdutoJpaRepository jpaProdutoRepository;

    @Override
    public void save(Produto produto) {
        
        ProdutoEntity entity = ProdutoMapper.toEntity(produto);
        jpaProdutoRepository.save(entity);
    }

    @Override
    public Produto findById(Long id) {
        return jpaProdutoRepository.findById(id)
                .map(ProdutoMapper::toDomain) 
                .orElse(null);
    }

    @Override
    public void deleteById(Long id) {
        jpaProdutoRepository.deleteById(id);
    }


    @Override
    public Produto findByName(String name) {
        return jpaProdutoRepository.findByNome(name)
                .map(ProdutoMapper::toDomain)
                .orElse(null);
    }

    @Override
    public Boolean existsByName(String name) {
        return jpaProdutoRepository.existsByNome(name);
    }

    @Override
    public Boolean existsById(Long id) {
        return jpaProdutoRepository.existsById(id);
    }

    @Override
    public List<Produto> findAll() {
        return this.jpaProdutoRepository.findAll()
        .stream()
        .map(ProdutoMapper::toDomain)
        .toList();
        
    }

    @Override
    public Produto saveProduto(Produto produto) {
        ProdutoEntity produtoEntity = ProdutoMapper.toEntity(produto);
        return ProdutoMapper.toDomain(produtoEntity);
    }
}