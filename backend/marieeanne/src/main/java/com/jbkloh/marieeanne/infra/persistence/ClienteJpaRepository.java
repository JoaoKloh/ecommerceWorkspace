package com.jbkloh.marieeanne.infra.persistence;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.jbkloh.marieeanne.infra.models.ClienteEntity;

@Repository
public interface ClienteJpaRepository extends JpaRepository<ClienteEntity, Long>{

    Optional<ClienteEntity> findByUsuarioId(Long user_id);
    
}
