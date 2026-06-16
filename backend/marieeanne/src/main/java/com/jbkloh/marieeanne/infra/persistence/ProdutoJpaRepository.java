package com.jbkloh.marieeanne.infra.persistence;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.jbkloh.marieeanne.infra.models.ProdutoEntity;

@Repository
public interface ProdutoJpaRepository extends JpaRepository<ProdutoEntity, Long> {
    boolean existsByNome(String nome);
    Optional<ProdutoEntity> findByNome(String nome);
}

