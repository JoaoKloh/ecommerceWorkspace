package com.jbkloh.marieeanne.infra.persistence;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.jbkloh.marieeanne.infra.models.PagamentoEntity;

@Repository
public interface PagamentoJpaRepository extends JpaRepository<PagamentoEntity, Long>{
    
}
