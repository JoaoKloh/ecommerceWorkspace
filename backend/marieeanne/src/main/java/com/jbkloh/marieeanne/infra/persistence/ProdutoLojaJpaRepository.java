package com.jbkloh.marieeanne.infra.persistence;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.jbkloh.marieeanne.infra.models.ProdutoLojaEntity;

public interface ProdutoLojaJpaRepository extends JpaRepository<ProdutoLojaEntity, Long> {
    
    @Query("SELECT pl FROM ProdutoLojaEntity pl WHERE pl.loja.id = :idLoja AND pl.produto.id = :idProduto")    
    Optional<ProdutoLojaEntity>findByLojaIdAndProdutoId(Long idLoja, Long idProduto);
    @Query("SELECT pl FROM ProdutoLojaEntity pl " +
           "INNER JOIN pl.produto p " +
           "WHERE pl.loja.id = :idLoja " +
           "AND p.estaDisponivel = true " +
           "AND pl.quantidade > 0")
    List<ProdutoLojaEntity> listarProdutosDisponiveisComEstoque(@Param("idLoja") Long idLoja);

}
