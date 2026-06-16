package com.jbkloh.marieeanne.infra.persistence;

import org.springframework.data.jpa.repository.JpaRepository;

import com.jbkloh.marieeanne.infra.models.EnderecoEntity;

public interface EnderecoJpaRepository extends JpaRepository<EnderecoEntity, Long> {
    
}
