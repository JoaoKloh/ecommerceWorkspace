package com.jbkloh.marieeanne.infra.adpters;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Component;

import com.jbkloh.marieeanne.core.models.ProdutoLoja;
import com.jbkloh.marieeanne.core.ports.ProdutoLojaRepositoryPort;
import com.jbkloh.marieeanne.infra.mappers.ProdutoLojaMapper;
import com.jbkloh.marieeanne.infra.models.ProdutoLojaEntity;
import com.jbkloh.marieeanne.infra.persistence.ProdutoLojaJpaRepository;

import lombok.AllArgsConstructor;


@Component
@AllArgsConstructor
public class ProdutoLojaAdapter implements ProdutoLojaRepositoryPort {
    
    private final ProdutoLojaJpaRepository produtoLojaJpaRepository;

    @Override
    public Optional<com.jbkloh.marieeanne.core.models.ProdutoLoja> findById(Long idProduto) {
       return produtoLojaJpaRepository.findById(idProduto)
       .map(ProdutoLojaMapper::toDomain);
    }

    @Override
    public Optional<com.jbkloh.marieeanne.core.models.ProdutoLoja> findByLojaIdAndProdutoId(Long idLoja, Long idProduto) {
        return produtoLojaJpaRepository.findByLojaIdAndProdutoId(idLoja,idProduto)
        .map(ProdutoLojaMapper::toDomain);
    }

    @Override
    public ProdutoLoja save(com.jbkloh.marieeanne.core.models.ProdutoLoja produtoLoja) {
        ProdutoLojaEntity entity = produtoLojaJpaRepository.save(ProdutoLojaMapper.toEntity(produtoLoja));
        return ProdutoLojaMapper.toDomain(entity);
    }
    @Override
    public List<ProdutoLoja> listarProdutosDisponiveisComEstoque(Long idLoja){
        return produtoLojaJpaRepository.listarProdutosDisponiveisComEstoque(idLoja)
        .stream()
        .map(ProdutoLojaMapper::toDomain)
        .toList();
    }

    @Override
    public List<ProdutoLoja> findAll() {
        return produtoLojaJpaRepository.findAll().stream()
        .map(ProdutoLojaMapper::toDomain)
        .toList();
    }

}
