package com.jbkloh.marieeanne.infra.persistence;    
import org.springframework.data.jpa.repository.JpaRepository;

import com.jbkloh.marieeanne.infra.models.LojaEntity;

public interface LojaJpaRepository extends JpaRepository<LojaEntity, Long> {
    
}
